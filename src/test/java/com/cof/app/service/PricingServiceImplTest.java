package com.cof.app.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cof.app.TestApp;
import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.PricingDataDaySegment;
import com.cof.app.model.TickersDailyPricingData;
import com.cof.app.model.quandl.QuandlPricingDataset;
import com.cof.app.model.quandl.QuandlTickerPricingData;
import com.cof.app.service.impl.PricingServiceImpl;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		classes = TestApp.class)
public class PricingServiceImplTest {

	public enum Ticker {
		COF, GOOGL, MSFT;
	}

	@Autowired
	private QuandlDriver driver;

	@Autowired
	private PricingServiceImpl pricingService;

	@Value("${defaults.startDate}")
	private String defaultStartDate;

	@Value("${defaults.endDate}")
	private String defaultEndDate;

	private QuandlTickerPricingData getQuandlTestData(String ticker, List<List<Object>> dailyData) {
		QuandlPricingDataset dataset = new QuandlPricingDataset(ticker, dailyData);
		QuandlTickerPricingData data = new QuandlTickerPricingData(dataset);
		return data;
	}

	private List<List<Object>> convertSegmentListToDailyData(
			List<PricingDataDaySegment> segmentList) {
		List<List<Object>> dailyData = new ArrayList<>();

		for (PricingDataDaySegment segment : segmentList) {
			List<Object> dayData = convertSegmentToList(segment);
			dailyData.add(dayData);
		}
		return dailyData;
	}

	private List<Object> convertSegmentToList(PricingDataDaySegment segment) {
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
		return dayData;
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
		QuandlTickerPricingData googlData = getQuandlTestData(googl, googlDailyData);

		List<PricingDataDaySegment> cofSegmentList = new ArrayList<>(2);
		cofSegmentList.add(new PricingDataDaySegment("2017-01-03", 88.55, 89.6, 87.79, 88.87,
				3441067d, 0d, 1d, 87.30123847595, 88.336431027048, 86.551956248488, 87.616725729618,
				3441067d));
		cofSegmentList.add(new PricingDataDaySegment("2017-01-04", 89.13, 90.77, 89.13, 90.3,
				2630905d, 0d, 1d, 87.873059123223, 89.489931298271, 87.873059123223,
				89.026559394447, 2630905d));

		List<List<Object>> cofDailyData = convertSegmentListToDailyData(cofSegmentList);
		QuandlTickerPricingData cofData = getQuandlTestData(cof, cofDailyData);

		String startDate = "2017-01-03";
		String endDate = "2017-01-04";

		when(driver.getPricingData(googl, startDate, endDate)).thenReturn(googlData);
		when(driver.getPricingData(cof, startDate, endDate)).thenReturn(cofData);

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

}
