package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpStatus;

/**
 * Thrown when document for a type cannot be found.
 */
public final class ResourceNotFoundException extends KatharsisMappableException {

	public ResourceNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND_404, ErrorData.builder().setTitle(message).setDetail(message)
				.setStatus(String.valueOf(HttpStatus.NOT_FOUND_404)).build());
	}

}