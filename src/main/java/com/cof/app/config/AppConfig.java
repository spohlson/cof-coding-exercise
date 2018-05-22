package com.cof.app.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.logging.LoggingInterceptor;
import com.cof.app.service.PricingService;
import com.cof.app.service.impl.QuandlService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	@Value("${debugLoggingEnabled}")
	private Boolean debugLoggingEnabled;

	@Bean
	PricingService pricingService() {
		return new QuandlService(quandlDriver());
	}

	@Bean
	QuandlDriver quandlDriver() {
		return new QuandlDriver(restTemplate());
	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(0, customMappingJackson2HttpMessageConverter());

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setOutputStreaming(false);

		BufferingClientHttpRequestFactory bufferingRequestFactory = new BufferingClientHttpRequestFactory(
				requestFactory);
		rest.setRequestFactory(bufferingRequestFactory);

		if (debugLoggingEnabled) {
			rest.getInterceptors().add(0, new LoggingInterceptor());
		}
		return rest;
	}

	@Bean
	public CustomMappingJackson2HttpMessageConverter customMappingJackson2HttpMessageConverter() {
		CustomMappingJackson2HttpMessageConverter converter = new CustomMappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper());

		List<MediaType> mediaTypes = Arrays.asList(MediaType.ALL);
		converter.setSupportedMediaTypes(mediaTypes);

		return converter;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

}
