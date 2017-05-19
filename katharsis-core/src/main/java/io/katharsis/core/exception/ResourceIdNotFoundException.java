package io.katharsis.core.exception;

/**
 * A document does not contain field annotated with JsonApiId annotation.
 */
public final class ResourceIdNotFoundException extends KatharsisInitializationException {

    public ResourceIdNotFoundException(String className) {
        super("Id field not found in class: " + className);
    }
}
