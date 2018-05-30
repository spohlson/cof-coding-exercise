package com.cof.app.controller;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cof.app.model.AverageMonthlyPricingData;
import com.cof.app.model.DailyPricingData;
import com.cof.app.model.MaxDailyProfits;
import com.cof.app.model.TickersBiggestLoser;
import com.cof.app.model.TickersBusiestDays;
import com.cof.app.service.PricingService;

@Validated
@RestController
@RequestMapping("/data")
public class PricingDataController {

	@Autowired
	private PricingService pricingService;

	@RequestMapping(value = "/daily", method = RequestMethod.GET)
	public ResponseEntity<?> getDailyPricingData(
			@NotEmpty
			@RequestParam(value = "tickers")
			List<String> tickers,
			@RequestParam(value = "start_date", defaultValue = "${defaults.startDate}")
			String startDate,
			@RequestParam(value = "end_date", defaultValue = "${defaults.endDate}")
			String endDate) {

		pricingService.validateRequestParams(tickers, startDate, endDate);

		DailyPricingData pricingData = pricingService.getDailyPricingDataForTickers(tickers,
				startDate, endDate);

		return new ResponseEntity<DailyPricingData>(pricingData, HttpStatus.OK);
	}

	@RequestMapping(value = "/daily/max-profit", method = RequestMethod.GET)
	public ResponseEntity<?> getMaxDailyProfit(
			@RequestParam(value = "tickers", defaultValue = "${defaults.tickers}")
			List<String> tickers,
			@RequestParam(value = "start_date", defaultValue = "${defaults.startDate}")
			String startDate,
			@RequestParam(value = "end_date", defaultValue = "${defaults.endDate}")
			String endDate) {

		pricingService.validateRequestParams(tickers, startDate, endDate);

		MaxDailyProfits maxDailyProfits = pricingService.getMaxDailyProfitForTickers(tickers,
				startDate, endDate);

		return new ResponseEntity<MaxDailyProfits>(maxDailyProfits, HttpStatus.OK);
	}

	@RequestMapping(value = "/daily/busy-day", method = RequestMethod.GET)
	public ResponseEntity<?> getBusiestDays(
			@RequestParam(value = "tickers", defaultValue = "${defaults.tickers}")
			List<String> tickers,
			@RequestParam(value = "start_date", defaultValue = "${defaults.startDate}")
			String startDate,
			@RequestParam(value = "end_date", defaultValue = "${defaults.endDate}")
			String endDate) {

		pricingService.validateRequestParams(tickers, startDate, endDate);

		TickersBusiestDays tickersBusiestDays = pricingService.getBusiestDaysForTickers(tickers,
				startDate, endDate);

		return new ResponseEntity<TickersBusiestDays>(tickersBusiestDays, HttpStatus.OK);
	}

	@RequestMapping(value = "/daily/loser", method = RequestMethod.GET)
	public ResponseEntity<?> getBiggestLoser(
			@RequestParam(value = "tickers", defaultValue = "${defaults.tickers}")
			List<String> tickers,
			@RequestParam(value = "start_date", defaultValue = "${defaults.startDate}")
			String startDate,
			@RequestParam(value = "end_date", defaultValue = "${defaults.endDate}")
			String endDate) {

		pricingService.validateRequestParams(tickers, startDate, endDate);

		TickersBiggestLoser biggestLoser = pricingService.getBiggestLoser(tickers, startDate,
				endDate);
		HttpStatus status = (biggestLoser.getBiggestLoser() == null) ? HttpStatus.NOT_FOUND
				: HttpStatus.OK;

		return new ResponseEntity<TickersBiggestLoser>(biggestLoser, status);
	}

	@RequestMapping(value = "/avg-monthly", method = RequestMethod.GET)
	public ResponseEntity<?> getAverageMonthlyPricingData(
			@RequestParam(value = "tickers", defaultValue = "${defaults.tickers}")
			List<String> tickers,
			@RequestParam(value = "start_date", defaultValue = "${defaults.startDate}")
			String startDate,
			@RequestParam(value = "end_date", defaultValue = "${defaults.endDate}")
			String endDate) {

		pricingService.validateRequestParams(tickers, startDate, endDate);

		AverageMonthlyPricingData avgMonthlyPricingData = pricingService
				.getAverageMonthlyPricingDataForTickers(tickers, startDate, endDate);

		return new ResponseEntity<AverageMonthlyPricingData>(avgMonthlyPricingData, HttpStatus.OK);
	}

}
