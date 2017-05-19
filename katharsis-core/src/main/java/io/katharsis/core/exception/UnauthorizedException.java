package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpStatus;

public class UnauthorizedException extends KatharsisMappableException {  // NOSONAR exception hierarchy deep but ok

	private static final String TITLE = "UNAUTHORIZED";

	public UnauthorizedException(String message) {
		super(HttpStatus.UNAUTHORIZED_401, ErrorData.builder().setTitle(TITLE).setDetail(message)
				.setStatus(String.valueOf(HttpStatus.UNAUTHORIZED_401)).build());
	}
}
