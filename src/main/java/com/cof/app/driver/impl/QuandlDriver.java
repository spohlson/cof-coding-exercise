package com.cof.app.driver.impl;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.PricingDriver;
import com.cof.app.model.ApiQueryParam;
import com.cof.app.model.QuandlPricingData;
import com.cof.app.model.RouteTemplate;

@Service
public class QuandlDriver extends PricingDriver {

	private static final Logger LOG = LoggerFactory.getLogger(QuandlDriver.class);

	@Autowired
	private QuandlConfig config;

	public QuandlDriver(RestTemplate rest) {
		this.rest = rest;
	}

	///////////// API Request Methods /////////////

	public QuandlPricingData getPricingData(String ticker, String startDate,
			String endDate) {
		URI uri = buildURI(RouteTemplate.PRICING_DATA, ticker, startDate, endDate);

		QuandlPricingData pricingData = rest
				.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(), QuandlPricingData.class)
				.getBody();

		return pricingData;
	}

	///////////// REST Builder Methods /////////////

	private URI buildURI(RouteTemplate routeTemplate, String ticker, String startDate,
			String endDate) {
		String api = config.getApiForRoute(routeTemplate);

		switch (routeTemplate) {
			case PRICING_DATA:
				api = String.format(api, ticker);
				break;
			default:
				LOG.error("RouteTemplate must be specified.");
				break;
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(api);
		appendDefaultParams(builder, startDate, endDate);

		URI uri = builder.build().encode().toUri();
		return uri;
	}

	private void appendDefaultParams(UriComponentsBuilder builder, String startDate,
			String endDate) {
		builder.queryParam(ApiQueryParam.API_KEY.toLowerCase(), config.getApiKey());
		builder.queryParam(ApiQueryParam.START_DATE.toLowerCase(), startDate);
		builder.queryParam(ApiQueryParam.END_DATE.toLowerCase(), endDate);
		builder.queryParam(ApiQueryParam.ORDER.toLowerCase(), "asc");
	}

}
