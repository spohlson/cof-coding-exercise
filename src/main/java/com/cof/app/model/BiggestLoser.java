package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BiggestLoser {

	private String ticker;
	@JsonProperty("num_of_losing_days")
	private int numOfLosingDays;

	public BiggestLoser(String ticker, int numOfLosingDays) {
		this.ticker = ticker;
		this.numOfLosingDays = numOfLosingDays;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public int getNumOfLosingDays() {
		return numOfLosingDays;
	}

	public void setNumOfLosingDays(int numOfLosingDays) {
		this.numOfLosingDays = numOfLosingDays;
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
		BiggestLoser other = (BiggestLoser) obj;
		if (numOfLosingDays != other.numOfLosingDays) {
			return false;
		}
		if (ticker == null) {
			if (other.ticker != null) {
				return false;
			}
		} else if (!ticker.equals(other.ticker)) {
			return false;
		}
		return true;
	}

}
