package io.katharsis.core.engine.internal.utils;

import io.katharsis.core.exception.KatharsisException;

/**
 * Indicate an exception when accessing document properties
 */
public class PropertyException extends KatharsisException {

    private final Class<?> resourceClass;
    private final String field;

    public PropertyException(Throwable cause, Class<?> resourceClass, String field) {
        super(cause.getMessage(), cause);
        this.resourceClass = resourceClass;
        this.field = field;
    }

    public PropertyException(String message, Class<?> resourceClass, String field) {
        super(message);
        this.resourceClass = resourceClass;
        this.field = field;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public String getField() {
        return field;
    }
}
