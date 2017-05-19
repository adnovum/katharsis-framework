package io.katharsis.rs.resource.exception.mapper;

import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.engine.error.JsonApiExceptionMapper;
import io.katharsis.legacy.queryParams.errorhandling.ExceptionMapperProvider;
import io.katharsis.core.engine.http.HttpStatus;
import io.katharsis.rs.resource.exception.ExampleException;

@ExceptionMapperProvider
public class ExampleExceptionMapper implements JsonApiExceptionMapper<ExampleException> {
    @Override
    public ErrorResponse toErrorResponse(ExampleException exception) {
        return ErrorResponse.builder()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500)
                .setSingleErrorData(ErrorData.builder()
                        .setTitle(exception.getTitle())
                        .setId(exception.getId())
                        .build())
                .build();
    }
}
