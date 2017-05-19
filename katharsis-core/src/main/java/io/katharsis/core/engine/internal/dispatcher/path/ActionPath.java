package io.katharsis.core.engine.internal.dispatcher.path;

/**
 * Represents a part of a path which relate a field of a document e.g. for /document/1/field the first element will be
 * an object of ResourcePath type and the second will be of FieldPath type.
 *
 * FieldPath can refer only to relationship fields.
 */
public class ActionPath extends JsonPath {

    public ActionPath(String elementName) {
        super(elementName);
    }

    @Override
    public boolean isCollection() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getResourceName() {
    	throw new UnsupportedOperationException();
    }
}
