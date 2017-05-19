package io.katharsis.core.engine.information.repository;

import java.util.Map;

import io.katharsis.core.engine.information.resource.ResourceInformation;

/**
 * Holds information about the type of a document document.
 */
public interface ResourceRepositoryInformation extends RepositoryInformation {

	/**
	 * @return information about the resources hold in this document
	 */
	ResourceInformation getResourceInformation();

	/**
	 * @return path from which the document is accessible
	 */
	String getPath();
	

	Map<String, RepositoryAction> getActions();
}
