package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "date", "profit" })
public class DailyProfit {

	private String date;
	private String profit;

	public DailyProfit(String date, String profit) {
		this.date = date;
		this.profit = profit;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

}
