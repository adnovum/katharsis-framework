package io.katharsis.core.exception;

/**
 * Thrown when document instance for a document cannot be found
 */
public final class RepositoryInstanceNotFoundException extends KatharsisMatchingException {

    public RepositoryInstanceNotFoundException(String missingRepositoryClassName) {
        super("Instance of the repository not found: " + missingRepositoryClassName);
    }
}