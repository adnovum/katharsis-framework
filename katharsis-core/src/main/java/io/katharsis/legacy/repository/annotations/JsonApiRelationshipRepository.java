package io.katharsis.legacy.repository.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Class annotated with this annotation will be treated as a relationship document class for a
 * {@link JsonApiResourceRepository#value()} property.
 * </p>
 * <p>
 * Repository methods defined in a class annotated by this <i>@interface</i> can throw <b>only</b> instances of
 * {@link RuntimeException}.
 * </p>
 * @see io.katharsis.legacy.repository.RelationshipRepository
 * @deprecated Make use of ResourceRepositoryV2 and related classes
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonApiRelationshipRepository {

    /**
     * source document model class type
     * @return class
     */
    Class<?> source();

    /**
     * target document model class type
     * @return class
     */
    Class<?> target();
}
