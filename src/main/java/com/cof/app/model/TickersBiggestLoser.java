package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TickersBiggestLoser {

	@JsonProperty("biggest_loser")
	private BiggestLoser biggestLoser;

	public TickersBiggestLoser() {

	}

	public TickersBiggestLoser(BiggestLoser biggestLoser) {
		this.biggestLoser = biggestLoser;
	}

	public BiggestLoser getBiggestLoser() {
		return biggestLoser;
	}

	public void setBiggestLoser(BiggestLoser biggestLoser) {
		this.biggestLoser = biggestLoser;
	}

}
