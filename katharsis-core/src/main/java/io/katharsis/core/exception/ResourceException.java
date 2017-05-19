package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpStatus;

/**
 * General exception regarding document building.
 */
public class ResourceException extends InternalServerErrorException {  // NOSONAR exception hierarchy deep but ok

    private static final String TITLE = "Resource error";

    public ResourceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR_500, ErrorData.builder()
                .setTitle(TITLE)
                .setDetail(message)
                .setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR_500))
                .build());
    }
    
    public ResourceException(String message, Exception cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR_500, ErrorData.builder()
                .setTitle(TITLE)
                .setDetail(message)
                .setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR_500))
                .build(), cause);
    }
}
