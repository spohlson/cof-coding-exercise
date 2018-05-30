package com.cof.app.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusiestDays {

	@JsonProperty("average_volume")
	private Double averageVolume;
	@JsonProperty("busiest_days")
	private List<BusyDay> busyDays;

	public BusiestDays(Double averageVolume, List<BusyDay> busyDays) {
		this.averageVolume = averageVolume;
		this.busyDays = busyDays;
	}

	public Double getAverageVolume() {
		return averageVolume;
	}

	public void setAverageVolume(Double averageVolume) {
		this.averageVolume = averageVolume;
	}

	public List<BusyDay> getBusyDays() {
		return busyDays;
	}

	public void setBusyDays(List<BusyDay> busyDays) {
		this.busyDays = busyDays;
	}

}
