package com.cof.app.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class MaxDailyProfits {

	private Map<String, DailyProfit> tickersMaxDailyProfits;

	public MaxDailyProfits(Map<String, DailyProfit> tickersMaxDailyProfits) {
		this.tickersMaxDailyProfits = tickersMaxDailyProfits;
	}

	@JsonAnyGetter
	public Map<String, DailyProfit> getTickersMaxDailyProfits() {
		return tickersMaxDailyProfits;
	}

	public void setTickersMaxDailyProfits(Map<String, DailyProfit> tickersMaxDailyProfits) {
		this.tickersMaxDailyProfits = tickersMaxDailyProfits;
	}

}
