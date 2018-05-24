package com.cof.app.service;

import java.util.List;

import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.MaxDailyProfits;

public interface PricingService {

	/**
	 * Validates the standard request params.
	 * 
	 * @param tickers
	 * @param start
	 * @param end
	 */
	void validateRequestParams(List<String> tickers, String start, String end);

	/**
	 * Gets the daily pricing data for the provided list of tickers between the
	 * specified date range.
	 * 
	 * @param tickers
	 * @param startDate
	 * @param endDate
	 * @return DailyPricingData
	 */
	DailyPricingData getDailyPricingDataForTickers(List<String> tickers, String startDate, String endDate);

	/**
	 * Gets the average monthly pricing data for the provided list of tickers
	 * between the specified date range.
	 * 
	 * @param tickers
	 * @param startDate
	 * @param endDate
	 * @return AverageMonthlyPricingData
	 */
	AverageMonthlyPricingData getAverageMonthlyPricingDataForTickers(List<String> tickers, String startDate,
			String endDate);

	/**
	 * Gets the max daily profit (highest amount of profit for each provided
	 * ticker if purchased at the day's low and sold at the day's high).
	 * 
	 * @param tickers
	 * @param startDate
	 * @param endDate
	 * @return max daily profit for each ticker
	 */
	MaxDailyProfits getMaxDailyProfitForTickers(List<String> tickers, String startDate,
			String endDate);

	void getBusiestDaysForTickers(List<String> tickers, String startDate, String endDate);

	void getBiggestLoser(List<String> tickers, String startDate, String endDate);

}
