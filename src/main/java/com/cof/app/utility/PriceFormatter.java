package com.cof.app.utility;

import java.math.BigDecimal;

public class PriceFormatter {

	private PriceFormatter() {

	}

	/**
	 * Formats double to decimal of 2 places and converts to String with
	 * currency symbol.
	 * 
	 * @param num
	 * @return price string
	 */
	public static String formatToPriceString(double num) {
		BigDecimal value = new BigDecimal(num);
		String price = value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
		return "$" + price;
	}

}
