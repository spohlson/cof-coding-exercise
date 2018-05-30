package com.cof.app.utility;

import java.math.BigDecimal;

public class PriceFormatter {

	private PriceFormatter() {

	}

	public static String formatToPriceString(double num) {
		BigDecimal value = new BigDecimal(num);
		String price = value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
		return "$" + price;
	}

}
