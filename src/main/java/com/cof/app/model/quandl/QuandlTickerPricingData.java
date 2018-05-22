package com.cof.app.model.quandl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuandlTickerPricingData {

	@JsonProperty("dataset")
	private QuandlPricingDataset dataset;

	public QuandlPricingDataset getDataset() {
		return dataset;
	}

	public void setDataset(QuandlPricingDataset dataset) {
		this.dataset = dataset;
	}

}
