package com.cof.app.model;

public enum ApiQueryParam {

	START_DATE,
	END_DATE,
	API_KEY,
	ORDER,
	TICKER;

	public String toLowerCase() {
		return name().toLowerCase();
	}

}
