package com.cof.app.service;

import java.util.List;

import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.MaxDailyProfits;
import com.cof.app.model.TickersBiggestLoser;
import com.cof.app.model.TickersBusiestDays;

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

	/**
	 * Shows which days generated unusually high activity (volume was more than
	 * 10% higher than the ticker's average) for the specified tickers.
	 * 
	 * @param tickers
	 * @param startDate
	 * @param endDate
	 */
	TickersBusiestDays getBusiestDaysForTickers(List<String> tickers, String startDate,
			String endDate);

	/**
	 * Determines which ticker had the most days where the closing price was
	 * lower than the opening price.
	 * 
	 * @param tickers
	 * @param startDate
	 * @param endDate
	 * @return biggest loser
	 */
	TickersBiggestLoser getBiggestLoser(List<String> tickers, String startDate, String endDate);

}
