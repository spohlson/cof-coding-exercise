package com.cof.app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "month", "average_open", "average_close" })
public class MonthPricingDataAverage {

	private String month;
	@JsonProperty("average_open")
	private String averageOpen;
	@JsonProperty("average_close")
	private String averageClose;

	public MonthPricingDataAverage(String month, double avgOpen, double avgClose) {
		this.month = month;
		averageOpen = formatAverage(avgOpen);
		averageClose = formatAverage(avgClose);
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

	private String formatAverage(double num) {
		BigDecimal value = new BigDecimal(num);
		return value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
	}

}
