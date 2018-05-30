package com.cof.app.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cof.app.config.QuandlConfig;
import com.cof.app.driver.QuandlDriver;
import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.BiggestLoser;
import com.cof.app.model.BusiestDays;
import com.cof.app.model.BusyDay;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.DailyProfit;
import com.cof.app.model.MaxDailyProfits;
import com.cof.app.model.MonthPricingDataAverage;
import com.cof.app.model.PricingDataDaySegment;
import com.cof.app.model.QuandlPricingDataColumn;
import com.cof.app.model.TickersBiggestLoser;
import com.cof.app.model.TickersBusiestDays;
import com.cof.app.model.TickersDailyPricingData;
import com.cof.app.model.TickersMonthlyAveragesData;
import com.cof.app.utility.PriceFormatter;

@RunWith(SpringJUnit4ClassRunner.class)
public class PricingServiceTest {

	public enum Ticker {
		GOOGL, COF, MSFT
	}

	@Mock
	private QuandlConfig config;

	@Mock
	private QuandlDriver driver;

	@InjectMocks
	private PricingServiceTestImpl pricingService;

	@Before
	public void initMockConfig() {
		QuandlPricingDataColumn[] columns = QuandlPricingDataColumn.values();
		when(config.getPricingDataColumnOrderSize()).thenReturn(columns.length);

		for (int i = 0; i < columns.length; i++) {
			QuandlPricingDataColumn column = columns[i];
			when(config.getQuandlPricingDataColumnByIndex(i)).thenReturn(column);
			when(config.getPricingDataColumnIndex(column)).thenReturn(i);
		}
		when(config.getDateFormat()).thenReturn("yyyy-MM-dd");
	}

	@Test
	public void testGetDailyPricingDataForTickers() {
		String googl = Ticker.GOOGL.name();
		String cof = Ticker.COF.name();

		List<String> tickers = new ArrayList<>();
		tickers.add(googl);
		tickers.add(cof);

		List<PricingDataDaySegment> googlSegmentList = new ArrayList<>(2);
		googlSegmentList.add(new PricingDataDaySegment("2017-01-03", 800.62, 811.435, 796.89,
				808.01, 1959033d, 0d, 1d, 800.62, 811.435, 796.89, 808.01, 1959033d));
		googlSegmentList.add(new PricingDataDaySegment("2017-01-04", 809.89, 813.43, 804.11, 807.77,
				1515339d, 0d, 1d, 809.89, 813.43, 804.11, 807.77, 1515339d));

		List<List<Object>> googlDailyData = convertSegmentListToDailyData(googlSegmentList);

		List<PricingDataDaySegment> cofSegmentList = new ArrayList<>(2);
		cofSegmentList.add(new PricingDataDaySegment("2017-01-03", 88.55, 89.6, 87.79, 88.87,
				3441067d, 0d, 1d, 87.30123847595, 88.336431027048, 86.551956248488, 87.616725729618,
				3441067d));
		cofSegmentList.add(new PricingDataDaySegment("2017-01-04", 89.13, 90.77, 89.13, 90.3,
				2630905d, 0d, 1d, 87.873059123223, 89.489931298271, 87.873059123223,
				89.026559394447, 2630905d));

		List<List<Object>> cofDailyData = convertSegmentListToDailyData(cofSegmentList);

		String startDate = "2017-01-03";
		String endDate = "2017-01-04";

		when(driver.getTickerDayDataList(googl, startDate, endDate)).thenReturn(googlDailyData);
		when(driver.getTickerDayDataList(cof, startDate, endDate)).thenReturn(cofDailyData);

		DailyPricingData output = pricingService.getDailyPricingDataForTickers(tickers, startDate,
				endDate);

		Assert.assertNotNull(output);
		Assert.assertTrue(StringUtils.equals(startDate, output.getStartDate()));
		Assert.assertTrue(StringUtils.equals(endDate, output.getEndDate()));

		TickersDailyPricingData tickerData = output.getTickerData();
		Assert.assertNotNull(tickerData);

		Map<String, List<PricingDataDaySegment>> tickerDailyPricingDataMap = tickerData
				.getTickerDailyPricingDataMap();
		Assert.assertTrue("Not all ticker data was found",
				tickers.size() == tickerDailyPricingDataMap.size());

		List<PricingDataDaySegment> cofOutputSegmentList = tickerDailyPricingDataMap.get(cof);
		Assert.assertNotNull(cofOutputSegmentList);
		Assert.assertTrue("Output cof segment daily data list differs from expected",
				cofSegmentList.equals(cofOutputSegmentList));

		List<PricingDataDaySegment> googlOutputSegmentList = tickerDailyPricingDataMap.get(googl);
		Assert.assertNotNull(googlOutputSegmentList);
		Assert.assertTrue("Output googl segment daily data list differs from expected",
				googlSegmentList.equals(googlOutputSegmentList));
	}

	@Test
	public void testGetAverageMonthlyPricingDataForTickers() {
		String googl = Ticker.GOOGL.name();
		String cof = Ticker.COF.name();

		List<String> tickers = new ArrayList<>();
		tickers.add(googl);
		tickers.add(cof);

		String startDate = "2017-01-01";
		String endDate = "2017-03-30";
		int numOfDaysInMonth = 30;

		List<String> months = new ArrayList<>();
		months.add("2017-01");
		months.add("2017-02");
		months.add("2017-03");

		String startMonth = months.get(0);
		String endMonth = months.get(months.size() - 1);

		List<MonthPricingDataAverage> expectedGooglAvgData = createAndMockAveragesData(months,
				googl,
				numOfDaysInMonth, startDate, endDate);
		List<MonthPricingDataAverage> expectedCofAvgData = createAndMockAveragesData(months, cof,
				numOfDaysInMonth, startDate, endDate);

		AverageMonthlyPricingData avgMonthlyData = pricingService
				.getAverageMonthlyPricingDataForTickers(tickers, startDate, endDate);
		Assert.assertNotNull(avgMonthlyData);
		Assert.assertTrue(startMonth.equals(avgMonthlyData.getStartMonth()));
		Assert.assertTrue(endMonth.equals(avgMonthlyData.getEndMonth()));

		TickersMonthlyAveragesData tickerData = avgMonthlyData.getTickerData();
		Assert.assertNotNull(tickerData);

		Map<String, List<MonthPricingDataAverage>> monthlyAveragesMap = tickerData
				.getMonthlyAveragesMap();
		Assert.assertFalse(MapUtils.isEmpty(monthlyAveragesMap));

		Assert.assertTrue(expectedGooglAvgData.equals(monthlyAveragesMap.get(googl)));
		Assert.assertTrue(expectedCofAvgData.equals(monthlyAveragesMap.get(cof)));
	}

	@Test
	public void testGetMaxDailyProfitForTickers() {
		String ticker = Ticker.COF.name();

		List<String> tickers = new ArrayList<>();
		tickers.add(ticker);

		String startDate = "2017-01-03";
		String endDate = "2017-01-04";

		double firstDayHigh = 20.50;
		double firstDayLow = 1.50;

		double maxProfit = firstDayHigh - firstDayLow;
		String maxProfitString = PriceFormatter.formatToPriceString(maxProfit);

		List<List<Object>> dailyData = new ArrayList<>(2);
		dailyData.add(createDayData(startDate, null, null, firstDayLow, firstDayHigh, null));
		dailyData.add(createDayData(endDate, null, null, 5.0, 6.0, null));

		when(driver.getTickerDayDataList(ticker, startDate, endDate)).thenReturn(dailyData);

		MaxDailyProfits maxDailyProfits = pricingService.getMaxDailyProfitForTickers(tickers,
				startDate, endDate);
		Assert.assertNotNull(maxDailyProfits);

		Map<String, DailyProfit> tickersMaxDailyProfits = maxDailyProfits
				.getTickersMaxDailyProfits();
		Assert.assertFalse(MapUtils.isEmpty(tickersMaxDailyProfits));

		DailyProfit dailyProfit = tickersMaxDailyProfits.get(ticker);
		Assert.assertNotNull(dailyProfit);
		Assert.assertTrue(startDate.equals(dailyProfit.getDate()));
		Assert.assertTrue(maxProfitString.equals(dailyProfit.getProfit()));
	}

	@Test
	public void testGetBusiestDaysForTickers() {
		String ticker = Ticker.COF.name();

		List<String> tickers = new ArrayList<>();
		tickers.add(ticker);

		String baseDate = "2017-01-0";
		String startDate = null;
		String endDate = null;

		double[] volumes = new double[] { 6, 12, 6, 7, 8, 3 };
		// 42 is the sum of all the volumes
		double avgVolume = 42 / volumes.length;
		double minBusyDayVolume = (avgVolume / 10) + avgVolume;

		List<List<Object>> dailyData = new ArrayList<>(volumes.length);
		List<BusyDay> expectedBusyDays = new ArrayList<>();

		for (int i = 0; i < volumes.length; i++) {
			double volume = volumes[i];
			String date = baseDate + (i + 1);

			dailyData.add(createDayData(date, null, null, null, null, volume));

			if (i == 0) {
				startDate = date;
			} else if (i == (volumes.length - 1)) {
				endDate = date;
			}

			if (volume > minBusyDayVolume) {
				expectedBusyDays.add(new BusyDay(date, volume));
			}
		}

		when(driver.getTickerDayDataList(ticker, startDate, endDate)).thenReturn(dailyData);

		TickersBusiestDays tickersBusiestDays = pricingService.getBusiestDaysForTickers(tickers,
				startDate, endDate);
		Assert.assertNotNull(tickersBusiestDays);

		Map<String, BusiestDays> tickersBusiestDaysMap = tickersBusiestDays
				.getTickersBusiestDaysMap();
		Assert.assertFalse(MapUtils.isEmpty(tickersBusiestDaysMap));

		BusiestDays busiestDays = tickersBusiestDaysMap.get(ticker);
		Assert.assertNotNull(busiestDays);

		Assert.assertTrue(avgVolume == busiestDays.getAverageVolume());
		Assert.assertTrue(expectedBusyDays.equals(busiestDays.getBusyDays()));
	}

	@Test
	public void testGetBiggestLoser() {
		String cof = Ticker.COF.name();
		String googl = Ticker.GOOGL.name();

		List<String> tickers = new ArrayList<>();
		tickers.add(cof);
		tickers.add(googl);

		String startDate = "2017-01-01";
		String endDate = "2017-01-03";

		// create 2 losing days for googl
		List<List<Object>> googlDailyData = new ArrayList<>(3);
		googlDailyData.add(createDayData(startDate, 1d, 5d, null, null, null));
		googlDailyData.add(createDayData("2017-01-02", 6d, 2d, null, null, null));
		googlDailyData.add(createDayData(endDate, 5d, 1d, null, null, null));

		when(driver.getTickerDayDataList(googl, startDate, endDate)).thenReturn(googlDailyData);

		// create 3 losing days for cof so it is the expected loser
		List<List<Object>> cofDailyData = new ArrayList<>(3);
		cofDailyData.add(createDayData(startDate, 8d, 5d, null, null, null));
		cofDailyData.add(createDayData("2017-01-02", 6d, 2d, null, null, null));
		cofDailyData.add(createDayData(endDate, 5d, 1d, null, null, null));

		when(driver.getTickerDayDataList(cof, startDate, endDate)).thenReturn(cofDailyData);

		BiggestLoser expectedLoser = new BiggestLoser(cof, 3);

		TickersBiggestLoser tickersBiggestLoser = pricingService.getBiggestLoser(tickers, startDate,
				endDate);
		Assert.assertNotNull(tickersBiggestLoser);

		BiggestLoser loser = tickersBiggestLoser.getBiggestLoser();
		Assert.assertNotNull(loser);
		Assert.assertTrue(expectedLoser.equals(loser));
	}

	///////////////// Test Helper Methods /////////////////

	/**
	 * Helper method for testGetDailyPricingDataForTickers() in order to build
	 * the daily data of a ticker from the expected segmentList
	 * 
	 * @param segmentList
	 * @return daily data
	 */
	private List<List<Object>> convertSegmentListToDailyData(
			List<PricingDataDaySegment> segmentList) {
		List<List<Object>> dailyData = new ArrayList<>(segmentList.size());

		for (PricingDataDaySegment segment : segmentList) {
			List<Object> dayData = new ArrayList<>(13);
			dayData.add(segment.getDate());
			dayData.add(segment.getOpen());
			dayData.add(segment.getHigh());
			dayData.add(segment.getLow());
			dayData.add(segment.getClose());
			dayData.add(segment.getVolume());
			dayData.add(segment.getExDividend());
			dayData.add(segment.getSplitRatio());
			dayData.add(segment.getAdjOpen());
			dayData.add(segment.getAdjHigh());
			dayData.add(segment.getAdjLow());
			dayData.add(segment.getAdjClose());
			dayData.add(segment.getAdjVolume());

			dailyData.add(dayData);
		}
		return dailyData;
	}

	/**
	 * Helper method for mocking the monthly averages data and returning the
	 * expected list of MonthPricingDataAverage for the ticker
	 * 
	 * @param months
	 * @param ticker
	 * @param numOfDaysInMonth
	 * @param startDate
	 * @param endDate
	 * @return List<MonthPricingDataAverage>
	 */
	private List<MonthPricingDataAverage> createAndMockAveragesData(List<String> months,
			String ticker, int numOfDaysInMonth, String startDate, String endDate) {
		int size = months.size();
		List<MonthPricingDataAverage> monthsAverages = new ArrayList<>(size);
		List<List<Object>> dailyData = new ArrayList<>();

		double min = 0.01d;
		double max = 15.99d;

		for (String month : months) {
			double openSum = 0.0;
			double closeSum = 0.0;

			int count = 1;

			while (count <= numOfDaysInMonth) {
				String date = month + ((count < 10) ? "-0" : "-") + String.valueOf(count);

				double open = getRandomDouble(min, max);
				openSum += open;

				double close = getRandomDouble(min, max);
				closeSum += close;

				List<Object> dayData = createDayData(date, open, close, null, null, null);
				dailyData.add(dayData);

				count++;
			}
			String avgOpen = PriceFormatter.formatToPriceString(openSum / numOfDaysInMonth);
			String avgClose = PriceFormatter.formatToPriceString(closeSum / numOfDaysInMonth);

			MonthPricingDataAverage monthAvg = new MonthPricingDataAverage(month, avgOpen,
					avgClose);

			monthsAverages.add(monthAvg);
		}
		when(driver.getTickerDayDataList(ticker, startDate, endDate)).thenReturn(dailyData);

		return monthsAverages;
	}

	/**
	 * Helper method for creating day data.
	 * 
	 * @param date
	 * @param open
	 * @param close
	 * @param low
	 * @param high
	 * @param volume
	 * @return day data
	 */
	private List<Object> createDayData(String date, Double open, Double close, Double low,
			Double high, Double volume) {
		QuandlPricingDataColumn[] columns = QuandlPricingDataColumn.values();
		List<Object> dayData = new ArrayList<>(columns.length);
		double defaultVal = 0.0;

		for (QuandlPricingDataColumn column : columns) {
			switch (column) {
				case DATE:
					dayData.add(date);
					break;
				case OPEN:
					open = (open != null) ? open : defaultVal;
					dayData.add(open);
					break;
				case CLOSE:
					close = (close != null) ? close : defaultVal;
					dayData.add(close);
					break;
				case HIGH:
					high = (high != null) ? high : defaultVal;
					dayData.add(high);
					break;
				case LOW:
					low = (low != null) ? low : defaultVal;
					dayData.add(low);
					break;
				case VOLUME:
					volume = (volume != null) ? volume : defaultVal;
					dayData.add(volume);
					break;
				default:
					dayData.add(defaultVal);
					break;
			}
		}
		return dayData;
	}

	/**
	 * Gets a random double in range.
	 * 
	 * @param min
	 * @param max
	 * @return random double
	 */
	private double getRandomDouble(double min, double max) {
		return min + ((max - min) * new Random().nextDouble());
	}

}
