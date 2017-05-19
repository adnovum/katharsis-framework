package io.katharsis.core.module.discovery;

import java.util.Set;

import io.katharsis.legacy.repository.RelationshipRepository;
import io.katharsis.legacy.repository.ResourceRepository;

public interface ResourceLookup {
	
	Set<Class<?>> getResourceClasses();
	
	/**
	 * Returns the document classes {@link ResourceRepository}, {@link RelationshipRepository}.
	 * 
	 * @return document classes
	 */
	Set<Class<?>> getResourceRepositoryClasses();
}
