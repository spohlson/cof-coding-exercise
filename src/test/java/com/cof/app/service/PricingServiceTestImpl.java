package com.cof.app.service;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.QuandlDriver;

/**
 * Class is used for testing purposed to allow a mocked driver be set in the
 * PricingServiceImpl.
 */
public class PricingServiceTestImpl extends PricingServiceImpl {

	public PricingServiceTestImpl(QuandlDriver driver) {
		super(driver);
	}

	public PricingServiceTestImpl(QuandlDriver driver, QuandlConfig config) {
		this(driver);
		super.setConfig(config);
	}

}
