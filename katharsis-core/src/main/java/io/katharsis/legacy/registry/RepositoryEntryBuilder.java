package io.katharsis.legacy.registry;

import java.util.List;

import io.katharsis.core.engine.registry.ResourceEntry;
import io.katharsis.core.module.discovery.ResourceLookup;
import io.katharsis.core.engine.registry.ResponseRelationshipEntry;

/**
 * Using class of this type it's possible to build instances of document
 * entries, which can be used by other parts of the library.
 */
public interface RepositoryEntryBuilder {

	ResourceEntry buildResourceRepository(ResourceLookup lookup, Class<?> resourceClass);

	List<ResponseRelationshipEntry> buildRelationshipRepositories(ResourceLookup lookup, Class<?> resourceClass);
}
