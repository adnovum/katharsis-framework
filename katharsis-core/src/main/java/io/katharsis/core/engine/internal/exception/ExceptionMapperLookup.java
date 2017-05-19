package io.katharsis.core.engine.internal.exception;

import java.util.Set;

import io.katharsis.core.engine.error.JsonApiExceptionMapper;

public interface ExceptionMapperLookup {

    Set<JsonApiExceptionMapper> getExceptionMappers();
}
