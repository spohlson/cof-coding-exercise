package com.cof.app.model.quandl;

public enum QuandlApiQueryParam {

	START_DATE,
	END_DATE,
	API_KEY,
	ORDER,
	TICKER;

	public String value() {
		return name().toLowerCase();
	}

}
