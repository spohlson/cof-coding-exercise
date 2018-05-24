package com.cof.app.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
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
		message += (StringUtils.isEmpty(value)) ? ". Cannot be empty/null."
				: ", with value: " + value;
		return message;
	}

}
