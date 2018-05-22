package com.cof.app.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.exception.InvalidParameterException;
import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.AveragePricingDataMonthSegment;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.PricingDataDaySegment;
import com.cof.app.model.TickersMonthlyAveragesData;
import com.cof.app.model.TickersPricingData;
import com.cof.app.model.quandl.QuandlApiQueryParam;
import com.cof.app.model.quandl.QuandlPricingDataColumn;
import com.cof.app.model.quandl.QuandlPricingDataset;
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
	public DailyPricingData getDailyPricingData(List<String> tickers,
			String startDate,
			String endDate) {
		if ((startDate == null) || (endDate == null)) {
			startDate = config.getDefaultStartDate();
			endDate = config.getDefaultEndDate();
		}
		validateDateRange(startDate, endDate);

		DailyPricingData dailyPricingData = new DailyPricingData(startDate, endDate);

		Map<String, List<PricingDataDaySegment>> tickersDailyPricingDataMap = new HashMap<>(
				tickers.size());

		for (String ticker : tickers) {
			List<PricingDataDaySegment> tickerDaySegmentList = getDailyPricingDataForTickerInDateRange(
					ticker, startDate, endDate);

			if (CollectionUtils.isEmpty(tickerDaySegmentList)) {
				// TODO throw some exception
			}
			tickersDailyPricingDataMap.put(ticker.toUpperCase(), tickerDaySegmentList);
		}
		TickersPricingData tickersPricingData = new TickersPricingData(tickersDailyPricingDataMap);
		dailyPricingData.setTickerData(tickersPricingData);

		return dailyPricingData;
	}

	private List<PricingDataDaySegment> getDailyPricingDataForTickerInDateRange(String ticker, String startDate,
			String endDate) {
		validateTicker(ticker);

		QuandlTickerPricingData quandlTickerPricingData = driver.getPricingData(ticker, startDate,
				endDate);

		if (quandlTickerPricingData != null) {
			QuandlPricingDataset quandlDataset = quandlTickerPricingData.getDataset();

			if (quandlDataset == null) {
				// TODO throw NOT FOUND EXCEPTION
				return null;
			}
			List<List<Object>> quandlDayDataList = quandlDataset.getData();

			if (CollectionUtils.isEmpty(quandlDayDataList)) {
				// TODO throw NOT FOUND EXCEPTION
				return null;
			}
			List<PricingDataDaySegment> tickerDaySegmentList = buildTickerPricingDataDaySegmentList(
					quandlDayDataList);

			return tickerDaySegmentList;
		}
		// TODO throw some NOT FOUND exception
		return null;
	}

	/**
	 * Iterates over Quandl's ticker daily pricing data converting the data to a
	 * List<PricingDataDaySegment>.
	 * 
	 * @param dataset
	 * @return List<PricingDataDaySegment>
	 */
	private List<PricingDataDaySegment> buildTickerPricingDataDaySegmentList(
			List<List<Object>> quandlDayDataList) {
		QuandlPricingDataColumn[] orderedDataColumns = QuandlPricingDataColumn.values();

		List<PricingDataDaySegment> dayPricingDataList = new ArrayList<>(quandlDayDataList.size());

		for (List<Object> quandlDayData : quandlDayDataList) {
			PricingDataDaySegment dayPricingData = new PricingDataDaySegment();

			for (int i = 0; i < orderedDataColumns.length; i++) {
				QuandlPricingDataColumn column = orderedDataColumns[i];
				Object obj = quandlDayData.get(i);

				dayPricingData.setQuandlPricingData(column, obj);
			}
			dayPricingDataList.add(dayPricingData);
		}
		return dayPricingDataList;
	}

	@Override
	public AverageMonthlyPricingData getAverageMonthlyPricingData() {
		String startDate = config.getDefaultStartDate();
		String endDate = config.getDefaultEndDate();
		List<String> tickers = config.getDefaultTickers();

		AverageMonthlyPricingData avgMonthlyPricingData = new AverageMonthlyPricingData(
				getMonthFromDate(startDate), getMonthFromDate(endDate));

		Map<String, List<AveragePricingDataMonthSegment>> tickersMonthlyAveragesMap = new HashMap<>(
				tickers.size());

		for (String ticker : tickers) {
			List<AveragePricingDataMonthSegment> tickerMonthlyAverages = getTickerMonthlyAverages(
					ticker, startDate, endDate);

			if (CollectionUtils.isEmpty(tickerMonthlyAverages)) {
				// TODO throw some exception
				return null;
			}
			tickersMonthlyAveragesMap.put(ticker.toUpperCase(), tickerMonthlyAverages);
		}
		TickersMonthlyAveragesData tickersMonthlyData = new TickersMonthlyAveragesData(
				tickersMonthlyAveragesMap);
		avgMonthlyPricingData.setTickerData(tickersMonthlyData);

		return avgMonthlyPricingData;
	}

	/**
	 * Gets Quandl's ticker pricing data for the specified ticker
	 * 
	 * @param ticker
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<AveragePricingDataMonthSegment> getTickerMonthlyAverages(String ticker,
			String startDate, String endDate) {
		QuandlTickerPricingData quandlTickerPricingData = driver.getPricingData(ticker, startDate,
				endDate);

		if (quandlTickerPricingData != null) {
			QuandlPricingDataset quandlDataset = quandlTickerPricingData.getDataset();

			if (quandlDataset == null) {
				// TODO throw NOT FOUND EXCEPTION
				return null;
			}
			List<List<Object>> quandlDayDataList = quandlDataset.getData();

			if (CollectionUtils.isEmpty(quandlDayDataList)) {
				// TODO throw NOT FOUND EXCEPTION
				return null;
			}
			List<AveragePricingDataMonthSegment> tickerMonthlyAverages = calculateTickerMonthlyAverages(
					quandlDayDataList);

			return tickerMonthlyAverages;
		} else {
			// TODO throw some NOT FOUND exception
			return null;
		}
	}

	/**
	 * Iterates through Quandl's ticker daily pricing data calculating the open
	 * and close averages for each month then returning those monthly averages
	 * as a List<AveragePricingDataMonthSegment>
	 * 
	 * @param quandlDayDataList
	 * @return List<AveragePricingDataMonthSegment>
	 */
	private List<AveragePricingDataMonthSegment> calculateTickerMonthlyAverages(
			List<List<Object>> quandlDayDataList) {
		int dateIndex = QuandlPricingDataColumn.DATE.getIndex();
		int openIndex = QuandlPricingDataColumn.OPEN.getIndex();
		int closeIndex = QuandlPricingDataColumn.CLOSE.getIndex();

		List<AveragePricingDataMonthSegment> avgMonthlyList = new ArrayList<>();

		String currentMonth = null;
		double monthOpenSum = 0;
		double monthCloseSum = 0;
		int numOfDaysInMonth = 0;

		for (List<Object> quandlDayData : quandlDayDataList) {
			String date = (String) quandlDayData.get(dateIndex);
			String month = getMonthFromDate(date);

			if (currentMonth == null) {
				currentMonth = month;
			}

			// check if month has moved onto the next
			if (!currentMonth.equals(month)) {
				// calculate current month's averages then add to avgMonthlyList
				double avgOpen = monthOpenSum / numOfDaysInMonth;
				double avgClose = monthCloseSum / numOfDaysInMonth;

				AveragePricingDataMonthSegment avgOpenCloseMonth = new AveragePricingDataMonthSegment(
						month, avgOpen, avgClose);

				avgMonthlyList.add(avgOpenCloseMonth);

				// move to next month and reset variables for avg calculations
				currentMonth = month;
				monthOpenSum = 0;
				monthCloseSum = 0;
				numOfDaysInMonth = 0;
			}
			monthOpenSum += (double) quandlDayData.get(openIndex);
			monthCloseSum += (double) quandlDayData.get(closeIndex);
			numOfDaysInMonth++;
		}
		return avgMonthlyList;
	}

	///////////// Validation Methods /////////////

	/**
	 * This method ensures ticker symbols are be between 1 and 5 characters in
	 * length which is standard.
	 * 
	 * @param ticker
	 */
	private void validateTicker(String ticker) {
		if (StringUtils.isEmpty(ticker) || (ticker.length() > 5)) {
			throw new InvalidParameterException(QuandlApiQueryParam.TICKER.value(), ticker);
		}
	}

	/**
	 * This method ensures the start/end arguments are valid dates and that the
	 * start date is before the end.
	 * 
	 * @param start
	 * @param end
	 */
	private void validateDateRange(String start, String end) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(config.getDateFormat());
		dateFormatter.setLenient(false);

		Date startDate = validateDate(dateFormatter, QuandlApiQueryParam.START_DATE, start);
		Date endDate = validateDate(dateFormatter, QuandlApiQueryParam.END_DATE, end);

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
	 * @return
	 */
	private Date validateDate(SimpleDateFormat dateFormatter, QuandlApiQueryParam param,
			String date) {
		try {
			return dateFormatter.parse(date);
		} catch (Exception e) {
			throw new InvalidParameterException(param.value(), date);
		}
	}

	///////////// Helper Methods /////////////

	private String getMonthFromDate(String date) {
		if (date.length() > 10) {
			throw new IllegalArgumentException("Invalid date format.");
		}
		return date.substring(0, 7);
	}

}
