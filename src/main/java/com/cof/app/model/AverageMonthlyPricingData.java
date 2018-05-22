package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "start_month", "end_month" })
public class AverageMonthlyPricingData {

	@JsonProperty("start_month")
	private String startMonth;
	@JsonProperty("end_month")
	private String endMonth;
	@JsonProperty("ticker_data")
	private TickersMonthlyAveragesData tickerData;

	public AverageMonthlyPricingData(String startMonth, String endMonth) {
		this.startMonth = startMonth;
		this.endMonth = endMonth;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public TickersMonthlyAveragesData getTickerData() {
		return tickerData;
	}

	public void setTickerData(TickersMonthlyAveragesData tickerData) {
		this.tickerData = tickerData;
	}

}
