package io.katharsis.core.engine.information.repository;

import io.katharsis.core.engine.information.resource.ResourceInformation;

/**
 * Holds information about the type of a document document.
 */
public interface RelationshipRepositoryInformation extends RepositoryInformation {

	/**
	 * @return information about the source of the relationship.
	 */
	ResourceInformation getSourceResourceInformation();

}
