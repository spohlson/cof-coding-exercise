package com.cof.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2136169577298199357L;

	public StockNotFoundException(String stockSymbol) {
		super("Could not find stock with symbol, " + stockSymbol + ".");
	}

}
