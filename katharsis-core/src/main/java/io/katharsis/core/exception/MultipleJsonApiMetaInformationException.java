package io.katharsis.core.exception;

/**
 * A document contains more then one field annotated with {@link io.katharsis.core.resource.annotations.JsonApiMetaInformation} annotation.
 */
public class MultipleJsonApiMetaInformationException extends KatharsisInitializationException {

    public MultipleJsonApiMetaInformationException(String className) {
        super("Duplicated meta fields found in class: " + className);
    }
}
