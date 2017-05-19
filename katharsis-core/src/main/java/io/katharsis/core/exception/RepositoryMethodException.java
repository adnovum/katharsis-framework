package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpStatus;

public class RepositoryMethodException extends InternalServerErrorException {  // NOSONAR exception hierarchy deep but ok
    private static final String TITLE = "Resource method error";

    public RepositoryMethodException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR_500, ErrorData.builder()
            .setTitle(TITLE)
            .setDetail(message)
            .setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR_500))
            .build());
    }
}
