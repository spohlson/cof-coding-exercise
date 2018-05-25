package com.cof.app.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.cof.app.driver.impl.QuandlDriver;

@Profile("test")
@Configuration
public class TestAppConfig {

	@Bean
	@Primary
	QuandlDriver quandlDriver() {
		return Mockito.mock(QuandlDriver.class);
	}

}
