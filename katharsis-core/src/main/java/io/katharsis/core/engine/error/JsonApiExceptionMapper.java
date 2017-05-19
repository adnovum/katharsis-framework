package io.katharsis.core.engine.error;

/**
 * Use {@link ExceptionMapper} instead which supports katharsis-client as well.
 */
@Deprecated
public interface JsonApiExceptionMapper<E extends Throwable> {

    ErrorResponse toErrorResponse(E exception);
}
