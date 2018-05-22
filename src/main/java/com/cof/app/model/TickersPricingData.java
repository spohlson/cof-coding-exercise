package com.cof.app.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class TickersPricingData {

	private Map<String, List<PricingDataDaySegment>> tickerDailyPricingDataMap;

	public TickersPricingData(Map<String, List<PricingDataDaySegment>> tickerPricingDataMap) {
		this.tickerDailyPricingDataMap = tickerPricingDataMap;
	}

	@JsonAnyGetter
	public Map<String, List<PricingDataDaySegment>> getTickerDailyPricingDataMap() {
		return tickerDailyPricingDataMap;
	}

	public void setTickerDailyPricingDataMap(
			Map<String, List<PricingDataDaySegment>> tickerPricingDataMap) {
		this.tickerDailyPricingDataMap = tickerPricingDataMap;
	}

}
