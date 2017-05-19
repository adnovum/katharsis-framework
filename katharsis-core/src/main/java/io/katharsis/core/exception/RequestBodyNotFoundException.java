package io.katharsis.core.exception;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.http.HttpStatus;

public class RequestBodyNotFoundException extends BadRequestException {  // NOSONAR exception hierarchy deep but ok

    private static final String TITLE = "Request body not found";

    public RequestBodyNotFoundException(HttpMethod method, String resourceName) {
        super(HttpStatus.BAD_REQUEST_400, ErrorData.builder()
            .setStatus(String.valueOf(HttpStatus.BAD_REQUEST_400))
            .setTitle(TITLE)
            .setDetail("Request body not found, " + method.name() + " method, resource name " + resourceName)
        .build());
    }

}
