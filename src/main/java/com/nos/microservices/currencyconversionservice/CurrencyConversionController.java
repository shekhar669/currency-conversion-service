package com.nos.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	@Autowired
	CurrencyExchangeProxy proxy;
	private Logger logger =LoggerFactory.getLogger(CurrencyConversionController.class);
	
	@GetMapping("currency-conversion-service/from/{from}/to/{to}/quantity/{quantity}")
	CurrencyConversionBean convertCurrency(HttpServletRequest req, @PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity, HttpServletResponse response) {

		Map<String, String> urivariable = new HashMap<>();
		urivariable.put("from", from);
		urivariable.put("to", to);

		ResponseEntity<CurrencyConversionBean> resBean = new RestTemplate().getForEntity(
				"http://localhost:5001/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
				urivariable);
		CurrencyConversionBean res = resBean.getBody();

		CurrencyConversionBean bean = new CurrencyConversionBean(from, to, res.getConversionMultiple(), quantity,
				quantity.multiply(res.getConversionMultiple()), res.getPort());

		return bean;
	}
	
	
	@GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	CurrencyConversionBean convertCurrencyFeign(HttpServletRequest req, @PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal quantity, HttpServletResponse response) {

		CurrencyConversionBean res = proxy.retrieveExchangeValue(from, to);
		logger.info("{}",res);
		CurrencyConversionBean bean = new CurrencyConversionBean(from, to, res.getConversionMultiple(), quantity,
				quantity.multiply(res.getConversionMultiple()), res.getPort());

		return bean;
	}

}
