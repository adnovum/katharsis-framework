package io.katharsis.core.engine.error.handlers;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.engine.error.ErrorResponseBuilder;
import io.katharsis.core.engine.error.JsonApiExceptionMapper;
import io.katharsis.legacy.queryParams.errorhandling.ExceptionMapperProvider;

@ExceptionMapperProvider
public class SomeExceptionMapper implements JsonApiExceptionMapper<SomeExceptionMapper.SomeException> {

    @Override
    public ErrorResponse toErrorResponse(SomeException Throwable) {
        return new ErrorResponseBuilder()
                .setStatus(500)
                .setSingleErrorData(ErrorData.builder()
                        .setTitle("hello")
                        .build())
                .build();
    }

    public static class SomeException extends RuntimeException {
    }
}

