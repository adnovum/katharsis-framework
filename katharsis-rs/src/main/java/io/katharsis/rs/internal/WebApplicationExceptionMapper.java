package io.katharsis.rs.internal;

import javax.ws.rs.WebApplicationException;

import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.engine.error.ExceptionMapperHelper;
import io.katharsis.core.engine.error.ExceptionMapper;

/**
 * Maps all exceptions which are subclasses of WebApplicationException to a JSON API exception response.<br />
 * The JSON API response contains the original exception information.
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	private static final String META_TYPE_VALUE = "WebApplicationException";

	@Override
	public ErrorResponse toErrorResponse(WebApplicationException exception) {
		return ExceptionMapperHelper.toErrorResponse(exception, exception.getResponse().getStatus(), exception.getClass().getSimpleName());
	}

	@Override
	public WebApplicationException fromErrorResponse(ErrorResponse errorResponse) {
		return new WebApplicationException(ExceptionMapperHelper.createErrorMessage(errorResponse));
	}

	@Override
	public boolean accepts(ErrorResponse errorResponse) {
		return ExceptionMapperHelper.accepts(errorResponse, errorResponse.getHttpStatus(), META_TYPE_VALUE);
	}
}
