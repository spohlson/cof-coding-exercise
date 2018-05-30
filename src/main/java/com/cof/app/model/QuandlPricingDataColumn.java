package com.cof.app.model;

import org.apache.commons.lang3.StringUtils;

public enum QuandlPricingDataColumn {

	DATE("Date"),
	OPEN("Open"),
	HIGH("High"),
	LOW("Low"),
	CLOSE("Close"),
	VOLUME("Volume"),
	EX_DIVIDEND("Ex-Dividend"),
	SPLIT_RATIO("Split Ratio"),
	ADJ_OPEN("Adj. Open"),
	ADJ_HIGH("Adj. High"),
	ADJ_LOW("Adj. Low"),
	ADJ_CLOSE("Adj. Close"),
	ADJ_VOLUME("Adj. Volume");

	private String value;

	private QuandlPricingDataColumn(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getValue();
	}

	public static QuandlPricingDataColumn getEnum(String value) {
		for (QuandlPricingDataColumn column : QuandlPricingDataColumn.values()) {

			if (StringUtils.equalsIgnoreCase(column.getValue(), value)) {
				return column;
			}
		}
		return null;
	}

}
