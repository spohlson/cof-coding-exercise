package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DayPricingData {

	private String date;
	private Double open;
	private Double close;
	private Double high;
	private Double low;
	private Double volume;
	@JsonProperty("ex-dividend")
	private Double exDividend;
	@JsonProperty("split_ratio")
	private Double splitRatio;
	@JsonProperty("adj_open")
	private Double adjOpen;
	@JsonProperty("adj_close")
	private Double adjClose;
	@JsonProperty("adj_high")
	private Double adjHigh;
	@JsonProperty("adj_low")
	private Double adjLow;
	@JsonProperty("adj_volume")
	private Double adjVolume;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getExDividend() {
		return exDividend;
	}

	public void setExDividend(Double exDividend) {
		this.exDividend = exDividend;
	}

	public Double getSplitRatio() {
		return splitRatio;
	}

	public void setSplitRatio(Double splitRatio) {
		this.splitRatio = splitRatio;
	}

	public Double getAdjOpen() {
		return adjOpen;
	}

	public void setAdjOpen(Double adjOpen) {
		this.adjOpen = adjOpen;
	}

	public Double getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(Double adjClose) {
		this.adjClose = adjClose;
	}

	public Double getAdjHigh() {
		return adjHigh;
	}

	public void setAdjHigh(Double adjHigh) {
		this.adjHigh = adjHigh;
	}

	public Double getAdjLow() {
		return adjLow;
	}

	public void setAdjLow(Double adjLow) {
		this.adjLow = adjLow;
	}

	public Double getAdjVolume() {
		return adjVolume;
	}

	public void setAdjVolume(Double adjVolume) {
		this.adjVolume = adjVolume;
	}

	public void setQuandlPricingData(QuandlPricingDataColumn column, Object obj) {
		switch (column) {
			case DATE:
				date = (String) obj;
				break;
			case OPEN:
				open = (Double) obj;
				break;
			case CLOSE:
				close = (Double) obj;
				break;
			case HIGH:
				high = (Double) obj;
				break;
			case LOW:
				low = (Double) obj;
				break;
			case VOLUME:
				volume = (Double) obj;
				break;
			case EX_DIVIDEND:
				exDividend = (Double) obj;
				break;
			case SPLIT_RATIO:
				splitRatio = (Double) obj;
				break;
			case ADJ_OPEN:
				adjOpen = (Double) obj;
				break;
			case ADJ_HIGH:
				adjHigh = (Double) obj;
				break;
			case ADJ_LOW:
				adjLow = (Double) obj;
				break;
			case ADJ_CLOSE:
				adjClose = (Double) obj;
				break;
			case ADJ_VOLUME:
				adjVolume = (Double) obj;
				break;
			default:
				break;
		}
	}

}
