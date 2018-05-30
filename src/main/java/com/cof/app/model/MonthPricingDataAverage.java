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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MonthPricingDataAverage other = (MonthPricingDataAverage) obj;
		if (averageClose == null) {
			if (other.averageClose != null) {
				return false;
			}
		} else if (!averageClose.equals(other.averageClose)) {
			return false;
		}
		if (averageOpen == null) {
			if (other.averageOpen != null) {
				return false;
			}
		} else if (!averageOpen.equals(other.averageOpen)) {
			return false;
		}
		if (month == null) {
			if (other.month != null) {
				return false;
			}
		} else if (!month.equals(other.month)) {
			return false;
		}
		return true;
	}

}
