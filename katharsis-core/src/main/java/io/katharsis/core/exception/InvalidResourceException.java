package io.katharsis.core.exception;

public class InvalidResourceException extends KatharsisInitializationException {

    public InvalidResourceException(String message) {
        super(message);
    }
    
    public InvalidResourceException(String message, Exception e) {
    	super(message, e);
    }
}
