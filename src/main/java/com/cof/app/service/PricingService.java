package com.cof.app.service;

import java.util.List;

import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.PricingData;

public abstract class PricingService {

	public abstract PricingData getPricingData(List<String> tickers, String startDate,
			String endDate);

	public abstract AverageMonthlyPricingData getAverageMonthlyPricingData();

}
