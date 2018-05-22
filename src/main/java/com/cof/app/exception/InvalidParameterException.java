package com.cof.app.exception;

import org.apache.commons.lang3.StringUtils;

public class InvalidParameterException extends IllegalArgumentException {

	private static final long serialVersionUID = 3403411197002373583L;

	public InvalidParameterException(String param, String value) {
		super(buildMessage(param, value));
	}

	public InvalidParameterException(String param, String value, String extraDetails) {
		super(buildMessage(param, value) + "\\n" + extraDetails);
	}

	private static String buildMessage(String param, String value) {
		String message = "Invalid parameter, " + param;

		if (StringUtils.isEmpty(value)) {
			message += ". Cannot be empty/null.";
		} else {
			message += ", of value: " + value;
		}
		return message;
	}

}
