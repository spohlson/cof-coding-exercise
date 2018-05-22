package com.cof.app.model.quandl;

import org.apache.commons.lang3.StringUtils;

public enum QuandlPricingDataColumn {

	DATE("Date", 0),
	OPEN("Open", 1),
	HIGH("High", 2),
	LOW("Low", 3),
	CLOSE("Close", 4),
	VOLUME("Volume", 5),
	EX_DIVIDEND("Ex-Dividend", 6),
	SPLIT_RATIO("Split Ratio", 7),
	ADJ_OPEN("Adj. Open", 8),
	ADJ_HIGH("Adj. High", 9),
	ADJ_LOW("Adj. Low", 10),
	ADJ_CLOSE("Adj. Close", 11),
	ADJ_VOLUME("Adj. Volume", 12);

	private String value;
	private int index;

	private QuandlPricingDataColumn(String value, int index) {
		this.value = value;
		this.index = index;
	}

	public String getValue() {
		return value;
	}
	
	public int getIndex() {
		return index;
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
