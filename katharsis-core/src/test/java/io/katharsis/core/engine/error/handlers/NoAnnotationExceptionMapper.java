package io.katharsis.core.engine.error.handlers;

import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.engine.error.JsonApiExceptionMapper;

public class NoAnnotationExceptionMapper implements JsonApiExceptionMapper<NoAnnotationExceptionMapper.ShouldNotAppearException> {
    @Override
    public ErrorResponse toErrorResponse(ShouldNotAppearException exception) {
        return ErrorResponse.builder().setStatus(500).build();
    }

    public static class ShouldNotAppearException extends RuntimeException {
    }
}
