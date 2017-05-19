package io.katharsis.core.exception;

/**
 * A field within a document was not found
 */
public final class ResourceFieldNotFoundException extends KatharsisMatchingException {

    public ResourceFieldNotFoundException(String message) {
        super(message);
    }
}
