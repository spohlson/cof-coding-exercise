package com.cof.app.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class TickersMonthlyAveragesData {

	private Map<String, List<MonthPricingDataAverage>> monthlyAveragesMap;

	public TickersMonthlyAveragesData(
			Map<String, List<MonthPricingDataAverage>> monthlyAveragesMap) {
		this.monthlyAveragesMap = monthlyAveragesMap;
	}

	@JsonAnyGetter
	public Map<String, List<MonthPricingDataAverage>> getMonthlyAveragesMap() {
		return monthlyAveragesMap;
	}

	public void setMonthlyAveragesMap(
			Map<String, List<MonthPricingDataAverage>> monthlyAveragesMap) {
		this.monthlyAveragesMap = monthlyAveragesMap;
	}

}
