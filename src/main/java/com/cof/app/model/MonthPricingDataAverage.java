package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "month", "average_open", "average_close" })
public class MonthPricingDataAverage {

	private String month;
	@JsonProperty("average_open")
	private String averageOpen;
	@JsonProperty("average_close")
	private String averageClose;

	public MonthPricingDataAverage(String month, String averageOpen, String averageClose) {
		this.month = month;
		this.averageOpen = averageOpen;
		this.averageClose = averageClose;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getAverageOpen() {
		return averageOpen;
	}

	public void setAverageOpen(String averageOpen) {
		this.averageOpen = averageOpen;
	}

	public String getAverageClose() {
		return averageClose;
	}

	public void setAverageClose(String averageClose) {
		this.averageClose = averageClose;
	}

}
