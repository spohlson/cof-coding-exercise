package com.cof.app.service;

import java.util.List;

import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DailyPricingData;

public interface PricingService {

	DailyPricingData getDailyPricingData(List<String> tickers, String startDate, String endDate);

	AverageMonthlyPricingData getAverageMonthlyPricingData();

}
