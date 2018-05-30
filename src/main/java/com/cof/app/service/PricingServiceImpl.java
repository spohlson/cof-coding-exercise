package com.cof.app.service;

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
import com.cof.app.driver.QuandlDriver;
import com.cof.app.exception.InvalidParameterException;
import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.BiggestLoser;
import com.cof.app.model.BusiestDays;
import com.cof.app.model.BusyDay;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.DailyProfit;
import com.cof.app.model.MaxDailyProfits;
import com.cof.app.model.MonthPricingDataAverage;
import com.cof.app.model.PricingDataDaySegment;
import com.cof.app.model.QuandlApiQueryParam;
import com.cof.app.model.QuandlPricingDataColumn;
import com.cof.app.model.TickersBiggestLoser;
import com.cof.app.model.TickersBusiestDays;
import com.cof.app.model.TickersDailyPricingData;
import com.cof.app.model.TickersMonthlyAveragesData;
import com.cof.app.utility.PriceFormatter;

@Service
public class PricingServiceImpl implements PricingService {

	private QuandlConfig config;
	private QuandlDriver driver;

	@Autowired
	public void setConfig(QuandlConfig config) {
		this.config = config;
	}

	public PricingServiceImpl(QuandlDriver driver) {
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
	 * Gets daily pricing data for the specified ticker in date range.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return A list of pricing data day segments for the specified ticker.
	 */
	private List<PricingDataDaySegment> getDailyPricingDataForTicker(String ticker,
			String startDate, String endDate) {
		List<List<Object>> quandlDayDataList = driver.getTickerDayDataList(ticker, startDate,
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
		List<List<Object>> quandlDayDataList = driver.getTickerDayDataList(ticker, startDate,
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
	private MonthPricingDataAverage calculateAndGetMonthAverage(String month, double openSum,
			double closeSum, double numOfDaysInMonth) {
		double avgOpenDouble = openSum / numOfDaysInMonth;
		double avgCloseDouble = closeSum / numOfDaysInMonth;

		String avgOpen = PriceFormatter.formatToPriceString(avgOpenDouble);
		String avgClose = PriceFormatter.formatToPriceString(avgCloseDouble);

		MonthPricingDataAverage monthAverage = new MonthPricingDataAverage(month, avgOpen,
				avgClose);
		return monthAverage;
	}

	@Override
	public MaxDailyProfits getMaxDailyProfitForTickers(List<String> tickers, String startDate,
			String endDate) {
		int dateIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.DATE);
		int lowIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.LOW);
		int highIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.HIGH);

		Map<String, DailyProfit> maxProfitsMap = new HashMap<>(tickers.size());

		for (String ticker : tickers) {
			DailyProfit maxDailyProfit = getMaxDailyProfitForTicker(ticker, startDate, endDate,
					dateIndex, lowIndex, highIndex);

			maxProfitsMap.put(ticker.toUpperCase(), maxDailyProfit);
		}
		MaxDailyProfits profits = new MaxDailyProfits(maxProfitsMap);
		return profits;
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
	 * @return max daily profit for the specified ticker
	 */
	private DailyProfit getMaxDailyProfitForTicker(String ticker, String startDate, String endDate,
			int dateIndex, int lowIndex, int highIndex) {
		List<List<Object>> quandlDayDataList = driver.getTickerDayDataList(ticker, startDate,
				endDate);

		List<Object> firstData = quandlDayDataList.get(0);

		double maxProfit = (double) firstData.get(highIndex) - (double) firstData.get(lowIndex);
		String maxProfitDate = (String) firstData.get(dateIndex);

		Iterator<List<Object>> it = quandlDayDataList.iterator();

		while (it.hasNext()) {
			List<Object> quandlDayData = it.next();

			double low = (double) quandlDayData.get(lowIndex);
			double high = (double) quandlDayData.get(highIndex);

			double profit = high - low;

			if (profit > maxProfit) {
				maxProfit = profit;
				maxProfitDate = (String) quandlDayData.get(dateIndex);
			}
		}
		String maxProfitString = PriceFormatter.formatToPriceString(maxProfit);

		DailyProfit dailyProfit = new DailyProfit(maxProfitDate, maxProfitString);
		return dailyProfit;
	}

	@Override
	public TickersBusiestDays getBusiestDaysForTickers(List<String> tickers, String startDate,
			String endDate) {
		Map<String, BusiestDays> tickersBusiestDaysMap = new HashMap<>(tickers.size());

		int dateIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.DATE);
		int volumeIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.VOLUME);

		for (String ticker : tickers) {
			BusiestDays busiestDays = getBusiestDaysForTicker(ticker, startDate, endDate, dateIndex,
					volumeIndex);

			tickersBusiestDaysMap.put(ticker.toUpperCase(), busiestDays);
		}
		TickersBusiestDays tickersBusiestDays = new TickersBusiestDays(tickersBusiestDaysMap);
		return tickersBusiestDays;
	}

	/**
	 * Determines the BusiestDays for the specified ticker.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @param dateIndex
	 * @param volumeIndex
	 * @return ticker's BusiestDays
	 */
	private BusiestDays getBusiestDaysForTicker(String ticker, String startDate, String endDate,
			int dateIndex, int volumeIndex) {
		List<List<Object>> quandlDayDataList = driver.getTickerDayDataList(ticker, startDate,
				endDate);
		double volumeSum = 0;
		double numOfDays = 0;

		List<Double> daysVolumeList = new ArrayList<>(quandlDayDataList.size());

		// iterate over day data list to calculate the average volume while
		// adding every day's volume to the daysVolumeList
		for (List<Object> dayData : quandlDayDataList) {
			double volume = (double) dayData.get(volumeIndex);

			daysVolumeList.add(volume);

			volumeSum += volume;
			numOfDays++;
		}
		double avgVolume = volumeSum / numOfDays;
		double minBusyDayVolume = (avgVolume / 10) + avgVolume;

		List<BusyDay> busyDays = new ArrayList<>();

		for (int i = 0; i < daysVolumeList.size(); i++) {
			double volume = daysVolumeList.get(i);

			// check if volume is 10% higher than the average volume
			if (volume > minBusyDayVolume) {
				List<Object> dayData = quandlDayDataList.get(i);
				String date = (String) dayData.get(dateIndex);

				busyDays.add(new BusyDay(date, volume));
			}
		}
		BusiestDays busiestDays = new BusiestDays(avgVolume, busyDays);
		return busiestDays;
	}

	@Override
	public TickersBiggestLoser getBiggestLoser(List<String> tickers, String startDate,
			String endDate) {
		int openIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.OPEN);
		int closeIndex = config.getPricingDataColumnIndex(QuandlPricingDataColumn.CLOSE);

		String biggestLoser = null;
		int maxLosingDays = 0;

		for (String ticker : tickers) {
			int numOfLosingDays = getNumOfLosingDaysForTicker(ticker, startDate, endDate, openIndex,
					closeIndex);

			if (numOfLosingDays > maxLosingDays) {
				maxLosingDays = numOfLosingDays;
				biggestLoser = ticker.toUpperCase();
			}
		}
		TickersBiggestLoser loser = new TickersBiggestLoser();

		if (biggestLoser != null) {
			loser.setBiggestLoser(new BiggestLoser(biggestLoser, maxLosingDays));
		}
		return loser;
	}

	/**
	 * Calculates the number of losing days (open is higher than close data) for
	 * the specified ticker in date range.
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @param openIndex
	 * @param closeIndex
	 * @return number of losing days for ticker
	 */
	private int getNumOfLosingDaysForTicker(String ticker, String startDate, String endDate,
			int openIndex, int closeIndex) {
		List<List<Object>> quandlDayDataList = driver.getTickerDayDataList(ticker, startDate,
				endDate);

		int numOfLosingDays = 0;

		for (List<Object> dayData : quandlDayDataList) {
			double open = (double) dayData.get(openIndex);
			double close = (double) dayData.get(closeIndex);

			if (open > close) {
				numOfLosingDays++;
			}
		}
		return numOfLosingDays;
	}

	///////////// Validation/Helper Methods /////////////

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
