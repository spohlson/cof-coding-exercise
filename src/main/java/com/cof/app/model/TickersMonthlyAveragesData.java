package com.cof.app.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class TickersMonthlyAveragesData {

	private Map<String, List<AveragePricingDataMonthSegment>> monthlyAveragesMap;

	public TickersMonthlyAveragesData(
			Map<String, List<AveragePricingDataMonthSegment>> monthlyAveragesMap) {
		this.monthlyAveragesMap = monthlyAveragesMap;
	}

	@JsonAnyGetter
	public Map<String, List<AveragePricingDataMonthSegment>> getMonthlyAveragesMap() {
		return monthlyAveragesMap;
	}

	public void setMonthlyAveragesMap(
			Map<String, List<AveragePricingDataMonthSegment>> monthlyAveragesMap) {
		this.monthlyAveragesMap = monthlyAveragesMap;
	}

}
