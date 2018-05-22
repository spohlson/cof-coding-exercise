package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "start_date", "end_date" })
public class DailyPricingData {

	@JsonProperty("start_date")
	private String startDate;
	@JsonProperty("end_date")
	private String endDate;
	@JsonProperty("ticker_data")
	private TickersPricingData tickerData;

	public DailyPricingData(String startDate, String endDate) {
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

	public TickersPricingData getTickerData() {
		return tickerData;
	}

	public void setTickerData(TickersPricingData tickerData) {
		this.tickerData = tickerData;
	}

}
