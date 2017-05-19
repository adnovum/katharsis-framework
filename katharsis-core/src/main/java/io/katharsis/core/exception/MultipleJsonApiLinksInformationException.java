package io.katharsis.core.exception;

/**
 * A document contains more then one field annotated with {@link io.katharsis.core.resource.annotations.JsonApiLinksInformation} annotation.
 */
public class MultipleJsonApiLinksInformationException extends KatharsisInitializationException {

    public MultipleJsonApiLinksInformationException(String className) {
        super("Duplicated links fields found in class: " + className);
    }
}
