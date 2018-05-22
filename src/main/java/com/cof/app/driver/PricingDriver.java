package com.cof.app.driver;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public abstract class PricingDriver {

	protected RestTemplate rest;

	protected HttpEntity<String> getDefaultHttpEntity() {
		return new HttpEntity<String>(getDefaultHeaders());
	}

	protected HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return headers;
	}

}
