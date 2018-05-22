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
import com.cof.app.model.ApiQueryParam;
import com.cof.app.model.AverageMonthlyOpenClose;
import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DayPricingData;
import com.cof.app.model.PricingData;
import com.cof.app.model.QuandlPricingData;
import com.cof.app.model.QuandlPricingDataColumn;
import com.cof.app.model.QuandlPricingDataset;
import com.cof.app.service.PricingService;

@Service
public class QuandlService extends PricingService {

	@Autowired
	private QuandlConfig config;

	private QuandlDriver driver;

	public QuandlService(QuandlDriver driver) {
		this.driver = driver;
	}

	@Override
	public PricingData getPricingData(List<String> tickers, String startDate,
			String endDate) {
		if ((startDate == null) || (endDate == null)) {
			startDate = config.getDefaultStartDate();
			endDate = config.getDefaultEndDate();
		}
		validateDateRange(startDate, endDate);

		PricingData pricingData = new PricingData(startDate, endDate);

		Map<String, List<DayPricingData>> tickerPricingDataMap = new HashMap<>(tickers.size());

		for (String ticker : tickers) {
			validateTicker(ticker);

			QuandlPricingData quandlTickerPricingData = driver.getPricingData(ticker, startDate,
					endDate);

			if (quandlTickerPricingData != null) {
				QuandlPricingDataset dataset = quandlTickerPricingData.getDataset();
				List<DayPricingData> dailyPricingData = buildDayPricingDataList(dataset);

				tickerPricingDataMap.put(ticker.toUpperCase(), dailyPricingData);
			} else {
				// TODO throw some NOT FOUND exception
			}
		}
		pricingData.setTickerPricingDataMap(tickerPricingDataMap);
		return pricingData;
	}

	@Override
	public AverageMonthlyPricingData getAverageMonthlyPricingData() {
		String startDate = config.getDefaultStartDate();
		String endDate = config.getDefaultEndDate();
		List<String> tickers = config.getDefaultTickers();

		AverageMonthlyPricingData avgMonthlyPricingData = new AverageMonthlyPricingData(
				getMonthFromDate(startDate), getMonthFromDate(endDate));

		Map<String, List<AverageMonthlyOpenClose>> monthlyAveragesMap = new HashMap<>(
				tickers.size());

		for (String ticker : tickers) {
			QuandlPricingData quandlTickerPricingData = driver.getPricingData(ticker, startDate,
					endDate);

			if (quandlTickerPricingData != null) {
				QuandlPricingDataset dataset = quandlTickerPricingData.getDataset();
				List<AverageMonthlyOpenClose> monthlyAverages = buildAverageMonthlyOpenCloseList(
						dataset);

				monthlyAveragesMap.put(ticker.toUpperCase(), monthlyAverages);
			} else {
				// TODO throw some NOT FOUND exception
			}
		}
		avgMonthlyPricingData.setMonthlyAveragesMap(monthlyAveragesMap);
		return avgMonthlyPricingData;
	}

	///////////// Pricing Data Builder Methods /////////////

	private List<AverageMonthlyOpenClose> buildAverageMonthlyOpenCloseList(
			QuandlPricingDataset dataset) {
		List<List<Object>> quandlDayDataList = dataset.getData();

		if (CollectionUtils.isEmpty(quandlDayDataList)) {
			// TODO throw NOT FOUND EXCEPTION
			return null;
		}
		int dateIndex = QuandlPricingDataColumn.DATE.getIndex();
		int openIndex = QuandlPricingDataColumn.OPEN.getIndex();
		int closeIndex = QuandlPricingDataColumn.CLOSE.getIndex();

		List<AverageMonthlyOpenClose> avgMonthlyList = new ArrayList<>();

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

			// if month has moved onto the next then calculate averages, create
			// the AverageMonthlyOpenClose object then add it to the list and
			// reset the monthly variables
			if (!currentMonth.equals(month)) {
				double avgOpen = monthOpenSum / numOfDaysInMonth;
				double avgClose = monthCloseSum / numOfDaysInMonth;
				AverageMonthlyOpenClose avgOpenCloseMonth = new AverageMonthlyOpenClose(month,
						avgOpen, avgClose);

				avgMonthlyList.add(avgOpenCloseMonth);

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

	private List<DayPricingData> buildDayPricingDataList(QuandlPricingDataset dataset) {
		List<List<Object>> quandlDayDataList = dataset.getData();

		if (CollectionUtils.isEmpty(quandlDayDataList)) {
			// TODO throw NOT FOUND EXCEPTION
			return null;
		}
		QuandlPricingDataColumn[] orderedDataColumns = QuandlPricingDataColumn.values();

		List<DayPricingData> dayPricingDataList = new ArrayList<>(quandlDayDataList.size());

		for (List<Object> quandlDayData : quandlDayDataList) {
			DayPricingData dayPricingData = new DayPricingData();

			for (int i = 0; i < orderedDataColumns.length; i++) {
				QuandlPricingDataColumn column = orderedDataColumns[i];
				Object obj = quandlDayData.get(i);

				dayPricingData.setQuandlPricingData(column, obj);
			}
			dayPricingDataList.add(dayPricingData);
		}
		return dayPricingDataList;
	}

	private String getMonthFromDate(String date) {
		if (date.length() > 10) {
			throw new IllegalArgumentException("Invalid date format.");
		}
		return date.substring(0, 7);
	}

	///////////// Validation Methods /////////////

	private void validateTicker(String ticker) {
		if (StringUtils.isEmpty(ticker) || (ticker.length() > 5)) {
			throw new InvalidParameterException(ApiQueryParam.TICKER.toLowerCase(), ticker);
		}
	}

	private void validateDateRange(String start, String end) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(config.getDateFormat());
		dateFormatter.setLenient(false);

		Date startDate = validateDate(dateFormatter, ApiQueryParam.START_DATE, start);
		Date endDate = validateDate(dateFormatter, ApiQueryParam.END_DATE, end);

		if (!startDate.before(endDate)) {
			throw new InvalidParameterException(ApiQueryParam.START_DATE.toLowerCase(), start,
					"Start date must be before end date.");
		}
	}

	private Date validateDate(SimpleDateFormat dateFormatter, ApiQueryParam param, String date) {
		try {
			return dateFormatter.parse(date);
		} catch (Exception e) {
			throw new InvalidParameterException(param.toLowerCase(), date);
		}
	}

}
