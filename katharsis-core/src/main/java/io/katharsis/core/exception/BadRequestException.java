package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpStatus;

public class BadRequestException extends KatharsisMappableException {

	private static final String TITLE = "BAD_REQUEST";

	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST_400, ErrorData.builder().setTitle(TITLE).setDetail(message)
				.setStatus(String.valueOf(HttpStatus.BAD_REQUEST_400)).build());
	}

	public BadRequestException(int httpStatus, ErrorData errorData) {
		super(httpStatus, errorData);
	}

	public BadRequestException(int httpStatus, ErrorData errorData, Throwable cause) {
		super(httpStatus, errorData, cause);
	}
}
