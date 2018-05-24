package com.cof.app.model;

public class DailyVolume {

	private String date;
	private double volume;

	public DailyVolume(String date, double volume) {
		this.date = date;
		this.volume = volume;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
