package com.cof.app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cof.app.driver.impl.QuandlDriver;
import com.cof.app.exception.InvalidParameterException;
import com.cof.app.model.quandl.QuandlTickerPricingData;
import com.cof.app.model.quandl.QuandlPricingDataset;

@RestController
@RequestMapping("/quandl")
public class QuandlPricingDataController {

	@Autowired
	private QuandlDriver driver;

	@RequestMapping(value = "/pricing", method = RequestMethod.GET)
	public ResponseEntity<?> getDailyPricingData(
			@NotEmpty
			@RequestParam(value = "tickers")
			List<String> tickers,
			@RequestParam(value = "start_date", required = false)
			String startDate,
			@RequestParam(value = "end_date", required = false)
			String endDate) {

		try {
			List<QuandlPricingDataset> datasets = new ArrayList<>(tickers.size());

			for (String ticker : tickers) {
				QuandlTickerPricingData pricingData = driver.getPricingData(ticker, startDate,
						endDate);
				QuandlPricingDataset dataset = pricingData.getDataset();

				datasets.add(dataset);
			}
			return new ResponseEntity<List<QuandlPricingDataset>>(datasets, HttpStatus.OK);
		} catch (InvalidParameterException e) {
			return new ResponseEntity<InvalidParameterException>(e, HttpStatus.BAD_REQUEST);
		}
	}

}
