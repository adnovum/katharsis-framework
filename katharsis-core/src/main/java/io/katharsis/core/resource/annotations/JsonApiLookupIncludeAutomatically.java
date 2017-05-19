package io.katharsis.core.resource.annotations;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.katharsis.legacy.queryParams.QueryParams;

/**
 * This annotation is used to make automatic value assignment using a defined relationship document if such document
 * is available. It can be used to leave document relationships from a document document not populated and make
 * Katharsis call either {@link io.katharsis.legacy.repository.RelationshipRepository#findOneTarget(Serializable, String, QueryParams)}
 * or {@link io.katharsis.legacy.repository.RelationshipRepository#findManyTargets(Serializable, String, QueryParams)}
 * depending on the multiplicity of the relationship.
 *
 * @deprecated It is recommended to to implement {@link JsonApiRelation}.
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonApiLookupIncludeAutomatically {

    /**
     * Defines whether Katharsis should overwrite the value of the related object on the document when setting inclusions
     *
     * @return true if the related object field is to be overwritten, false otherwise
     */
    boolean overwrite() default false;
}
