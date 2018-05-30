package com.cof.app.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class TickersBusiestDays {

	private Map<String, BusiestDays> tickersBusiestDaysMap;

	public TickersBusiestDays(Map<String, BusiestDays> tickersBusiestDaysMap) {
		this.tickersBusiestDaysMap = tickersBusiestDaysMap;
	}

	@JsonAnyGetter
	public Map<String, BusiestDays> getTickersBusiestDaysMap() {
		return tickersBusiestDaysMap;
	}

	public void setTickersBusiestDaysMap(Map<String, BusiestDays> tickersBusiestDaysMap) {
		this.tickersBusiestDaysMap = tickersBusiestDaysMap;
	}

}
