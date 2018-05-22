package com.cof.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuandlPricingData {

	@JsonProperty("dataset")
	private QuandlPricingDataset dataset;

	public QuandlPricingDataset getDataset() {
		return dataset;
	}

	public void setDataset(QuandlPricingDataset dataset) {
		this.dataset = dataset;
	}

}
