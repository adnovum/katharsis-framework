package io.katharsis.core.engine.registry;

/**
 * Identifies a relationship document entry
 */
public interface ResponseRelationshipEntry {

    /**
     * @return target class
     */
    Class<?> getTargetAffiliation();
}
