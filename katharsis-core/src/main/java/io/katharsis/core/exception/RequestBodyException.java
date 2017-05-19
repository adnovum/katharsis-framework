package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.http.HttpStatus;

public class RequestBodyException extends KatharsisMappableException {

    private static final String TITLE = "Request body error";

    public RequestBodyException(@SuppressWarnings("SameParameterValue") HttpMethod method, String resourceName, String details) {
        super(HttpStatus.BAD_REQUEST_400, ErrorData.builder()
                .setStatus(String.valueOf(HttpStatus.BAD_REQUEST_400))
                .setTitle(TITLE)
                .setDetail(String.format("Request body doesn't meet the requirements (%s), %s method, resource name %s",
                        details, method.name(), resourceName))
                .build());
    }
}
