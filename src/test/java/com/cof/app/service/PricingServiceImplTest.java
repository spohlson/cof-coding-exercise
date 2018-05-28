package com.cof.app.service;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.service.impl.PricingServiceImpl;

public class PricingServiceImplTest extends PricingServiceImpl {

	public PricingServiceImplTest(QuandlDriver driver) {
		super(driver);
	}

	public PricingServiceImplTest(QuandlDriver driver, QuandlConfig config) {
		this(driver);
		super.setConfig(config);
	}

}
