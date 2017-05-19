package io.katharsis.core.engine.information.repository;

import io.katharsis.core.engine.information.resource.ResourceInformation;

/**
 * Holds information about the type of a document.
 */
public interface RepositoryInformation {

	Class<?> getRepositoryClass();

	/**
	 * @return information about the resources hold in this document
	 */
	ResourceInformation getResourceInformation();
}
