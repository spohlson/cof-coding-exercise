package com.cof.app.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "start_month", "end_month" })
public class AverageMonthlyPricingData {

	@JsonProperty("start_month")
	private String startMonth;
	@JsonProperty("end_month")
	private String endMonth;
	private Map<String, List<AverageMonthlyOpenClose>> monthlyAveragesMap;

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

	@JsonAnyGetter
	public Map<String, List<AverageMonthlyOpenClose>> getMonthlyAveragesMap() {
		return monthlyAveragesMap;
	}

	public void setMonthlyAveragesMap(
			Map<String, List<AverageMonthlyOpenClose>> monthlyAveragesMap) {
		this.monthlyAveragesMap = monthlyAveragesMap;
	}

}
