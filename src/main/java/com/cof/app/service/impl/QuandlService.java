package com.cof.app.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.exception.DataNotFoundException;
import com.cof.app.exception.InvalidParameterException;
import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.DailyProfit;
import com.cof.app.model.MaxDailyProfits;
import com.cof.app.model.MonthPricingDataAverage;
import com.cof.app.model.PricingDataDaySegment;
import com.cof.app.model.TickersDailyPricingData;
import com.cof.app.model.TickersMonthlyAveragesData;
import com.cof.app.model.quandl.QuandlApiQueryParam;
import com.cof.app.model.quandl.QuandlPricingDataColumn;
import com.cof.app.model.quandl.QuandlTickerPricingData;
import com.cof.app.service.PricingService;

@Service
public class QuandlService implements PricingService {

	@Autowired
	private QuandlConfig config;

	private QuandlDriver driver;

	public QuandlService(QuandlDriver driver) {
		this.driver = driver;
	}

	@Override
	public DailyPricingData getDailyPricingDataForTickers(List<String> tickers, String startDate,
			String endDate) {
		DailyPricingData dailyPricingData = new DailyPricingData(startDate, endDate);

		Map<String, List<PricingDataDaySegment>> tickersDailyPricingDataMap = new HashMap<>(
				tickers.size());

		for (String ticker : tickers) {
			List<PricingDataDaySegment> tickerDaySegmentList = getDailyPricingDataForTicker(ticker,
					startDate, endDate);

			tickersDailyPricingDataMap.put(ticker.toUpperCase(), tickerDaySegmentList);
		}
		TickersDailyPricingData tickersPricingData = new TickersDailyPricingData(
				tickersDailyPricingDataMap);

		dailyPricingData.setTickerData(tickersPricingData);

		return dailyPricingData;
	}

	/**
	 * Gets daily pricing data for the provided ticker between the specified
	 * date range.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return A list of pricing data day segments for the specified ticker.
	 */
	private List<PricingDataDaySegment> getDailyPricingDataForTicker(String ticker,
			String startDate, String endDate) {
		List<List<Object>> quandlDayDataList = getValidQuandlDayDataList(ticker, startDate,
				endDate);

		List<PricingDataDaySegment> daySegments = new ArrayList<>(quandlDayDataList.size());

		for (List<Object> quandlDayData : quandlDayDataList) {
			PricingDataDaySegment daySegment = new PricingDataDaySegment();

			for (int i = 0; i < config.getPricingDataColumnOrderSize(); i++) {
				Object obj = quandlDayData.get(i);
				QuandlPricingDataColumn column = config.getQuandlPricingDataColumnByIndex(i);

				daySegment.setQuandlPricingData(column, obj);
			}
			daySegments.add(daySegment);
		}
		return daySegments;
	}

	@Override
	public AverageMonthlyPricingData getAverageMonthlyPricingDataForTickers(List<String> tickers,
			String startDate, String endDate) {
		// get indexes of Quandl's pricing data column to fetch the required
		// data to calculate averages
		int dateIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.DATE);
		int openIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.OPEN);
		int closeIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.CLOSE);

		AverageMonthlyPricingData avgMonthlyPricingData = new AverageMonthlyPricingData(
				getMonthFromDate(startDate), getMonthFromDate(endDate));

		Map<String, List<MonthPricingDataAverage>> allMonthlyAveragesMap = new HashMap<>(
				tickers.size());

		for (String ticker : tickers) {
			List<MonthPricingDataAverage> tickerMonthlyAverages = getAverageMonthlyPricingDataForTicker(
					ticker, startDate, endDate, dateIndex, openIndex, closeIndex);

			allMonthlyAveragesMap.put(ticker.toUpperCase(), tickerMonthlyAverages);
		}
		TickersMonthlyAveragesData tickersMonthlyData = new TickersMonthlyAveragesData(
				allMonthlyAveragesMap);

		avgMonthlyPricingData.setTickerData(tickersMonthlyData);

		return avgMonthlyPricingData;
	}

	/**
	 * Fetches a ticker's Quandl daily pricing data for the specified date range
	 * then calculates and returns its monthly average open/close pricing.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @param dateIndex
	 * @param openIndex
	 * @param closeIndex
	 * @return A list of average pricing data month segments for the specified
	 *         ticker.
	 */
	private List<MonthPricingDataAverage> getAverageMonthlyPricingDataForTicker(String ticker,
			String startDate, String endDate, int dateIndex, int openIndex, int closeIndex) {
		List<List<Object>> quandlDayDataList = getValidQuandlDayDataList(ticker, startDate,
				endDate);

		List<MonthPricingDataAverage> monthlyAverages = new ArrayList<>();

		// set currentMonth to first month in quandlDayDataList
		String currentMonth = getMonthFromDate((String) quandlDayDataList.get(0).get(dateIndex));
		double monthOpenSum = 0;
		double monthCloseSum = 0;
		int numOfDaysInMonth = 0;

		Iterator<List<Object>> it = quandlDayDataList.iterator();

		while (it.hasNext()) {
			List<Object> quandlDayData = it.next();

			String date = (String) quandlDayData.get(dateIndex);
			String month = getMonthFromDate(date);

			// calculate current month's averages and add to avgMonthlyList if
			// month has moved onto the next or if we're at the end of the
			// quandlDayDataList
			if (!currentMonth.equals(month)) {
				MonthPricingDataAverage monthAverage = calculateAndGetMonthAverage(currentMonth, monthOpenSum,
						monthCloseSum, numOfDaysInMonth);

				monthlyAverages.add(monthAverage);

				// move to next month and reset variables for avg calculations
				currentMonth = month;
				monthOpenSum = 0;
				monthCloseSum = 0;
				numOfDaysInMonth = 0;
			}
			monthOpenSum += (double) quandlDayData.get(openIndex);
			monthCloseSum += (double) quandlDayData.get(closeIndex);
			numOfDaysInMonth++;

			// check if at end of quandl data, if so then calculate the last
			// month's averages and add to list
			if (!it.hasNext()) {
				MonthPricingDataAverage monthAverage = calculateAndGetMonthAverage(currentMonth, monthOpenSum,
						monthCloseSum, numOfDaysInMonth);

				monthlyAverages.add(monthAverage);
			}
		}
		return monthlyAverages;
	}

	/**
	 * Calculates a month's average open and close pricing and creates/returns
	 * that information as MonthPricingDataAverage object.
	 * 
	 * @param month
	 * @param openSum
	 * @param closeSum
	 * @param numOfDaysInMonth
	 * @return Month's average open and close prices
	 */
	private MonthPricingDataAverage calculateAndGetMonthAverage(String month, double openSum, double closeSum,
			double numOfDaysInMonth) {
		double avgOpen = openSum / numOfDaysInMonth;
		double avgClose = closeSum / numOfDaysInMonth;

		MonthPricingDataAverage monthAverage = new MonthPricingDataAverage(month, avgOpen,
				avgClose);
		return monthAverage;
	}

	// gets highest amount of profit for each ticker if purchased at the day's
	// low and sold at the day's high
	@Override
	public MaxDailyProfits getMaxDailyProfitForTickers(List<String> tickers, String startDate,
			String endDate) {
		int dateIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.DATE);
		int lowIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.LOW);
		int highIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.HIGH);

		// MaxDailyProfits maxProfits = new MaxDailyProfits();

		Map<String, DailyProfit> maxProfitsMap = new HashMap<>(tickers.size());

		for (String ticker : tickers) {

		}

		return null;
	}

	/**
	 * Gets highest amount of profit for ticker if purchased at the day's low
	 * and sold at the day's high.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @param dateIndex
	 * @param lowIndex
	 * @param highIndex
	 * @return
	 */
	private DailyProfit getMaxDailyProfitForTicker(String ticker, String startDate, String endDate,
			int dateIndex, int lowIndex, int highIndex) {
		List<List<Object>> quandlDayDataList = getValidQuandlDayDataList(ticker, startDate,
				endDate);

		double maxProfit = 0;
		String maxProfitDate;

		Iterator<List<Object>> it = quandlDayDataList.iterator();

		while (it.hasNext()) {
			List<Object> quandlDayData = it.next();

			String date = (String) quandlDayData.get(dateIndex);
		}

		return null;
	}

	@Override
	public void getBusiestDaysForTickers(List<String> tickers, String startDate, String endDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getBiggestLoser(List<String> tickers, String startDate, String endDate) {
		// TODO Auto-generated method stub

	}

	///////////// Validation/Helper Methods /////////////

	/**
	 * Fetches a ticker's Quandl pricing data from the driver and checks if any
	 * data was returned/included, throws DataNotFoundException if not.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return A list of each day's pricing data which is a list of objects for
	 *         the specified ticker.
	 */
	private List<List<Object>> getValidQuandlDayDataList(String ticker, String startDate,
			String endDate) {
		QuandlTickerPricingData quandlTickerPricingData = driver.getPricingData(ticker, startDate,
				endDate);

		if ((quandlTickerPricingData == null) || (quandlTickerPricingData.getDataset() == null)
				|| CollectionUtils.isEmpty(quandlTickerPricingData.getDataset().getData())) {
			throw new DataNotFoundException(ticker + "'s pricing data between " + startDate
					+ " and " + endDate + " not found.");
		}
		return quandlTickerPricingData.getDataset().getData();
	}

	@Override
	public void validateRequestParams(List<String> tickers, String start, String end) {
		validateTickers(tickers);
		validateDateRange(start, end);
	}

	/**
	 * Checks if tickers are in a valid format for Quandl's pricing service.
	 * 
	 * @param tickers
	 */
	private void validateTickers(List<String> tickers) {
		if (CollectionUtils.isEmpty(tickers)) {
			throw new InvalidParameterException(QuandlApiQueryParam.TICKER.value() + "'s", null);
		}

		for (String ticker : tickers) {

			if (StringUtils.isEmpty(ticker) || (ticker.length() > 5)) {
				throw new InvalidParameterException(QuandlApiQueryParam.TICKER.value(), ticker);
			}
		}
	}

	/**
	 * Checks whether startDate and endDate parameters are in a valid date
	 * format, are actual dates, and that the start date is before the end date.
	 * 
	 * @param start
	 * @param end
	 */
	private void validateDateRange(String start, String end) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(config.getDateFormat());
		dateFormatter.setLenient(false);

		Date startDate = getValidDate(dateFormatter, QuandlApiQueryParam.START_DATE, start);
		Date endDate = getValidDate(dateFormatter, QuandlApiQueryParam.END_DATE, end);

		if (!startDate.before(endDate)) {
			throw new InvalidParameterException(QuandlApiQueryParam.START_DATE.value(), start,
					"Start date must be before end date.");
		}
	}

	/**
	 * This method ensures the date argument is an actual date and in the
	 * correct format for Quandl's APIs.
	 * 
	 * @param dateFormatter
	 * @param param
	 * @param date
	 * @return The Date object of the specified date.
	 */
	private Date getValidDate(SimpleDateFormat dateFormatter, QuandlApiQueryParam param,
			String date) {
		try {
			return dateFormatter.parse(date);
		} catch (Exception e) {
			throw new InvalidParameterException(param.value(), date);
		}
	}

	/**
	 * Cuts day from date. Ex. "2018-04-12" input returns "2018-04".
	 * 
	 * @param date
	 * @return The year & month of the date argument.
	 */
	private String getMonthFromDate(String date) {
		if (date.length() > 10) {
			throw new IllegalArgumentException("Invalid date format.");
		}
		return date.substring(0, 7);
	}

}
