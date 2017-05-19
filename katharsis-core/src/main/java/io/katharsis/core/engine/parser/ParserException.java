package io.katharsis.core.engine.parser;

import io.katharsis.core.exception.KatharsisMatchingException;

/**
 * Thrown when parser exception occurs.
 */
public class ParserException extends KatharsisMatchingException {

    public ParserException(String message) {
        super(message);
    }
    
    public ParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
