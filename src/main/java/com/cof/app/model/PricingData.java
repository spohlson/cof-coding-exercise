package com.cof.app.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "start_date", "end_date" })
public class PricingData {

	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	private Map<String, List<DayPricingData>> tickerPricingDataMap;

	public PricingData(String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@JsonAnyGetter
	public Map<String, List<DayPricingData>> getTickerPricingDataMap() {
		return tickerPricingDataMap;
	}

	public void setTickerPricingDataMap(Map<String, List<DayPricingData>> tickerPricingDataMap) {
		this.tickerPricingDataMap = tickerPricingDataMap;
	}

}
