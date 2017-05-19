package io.katharsis.core.engine.internal.information.repository;

import io.katharsis.core.engine.information.repository.RelationshipRepositoryInformation;
import io.katharsis.core.engine.information.resource.ResourceInformation;

public class RelationshipRepositoryInformationImpl extends RepositoryInformationImpl implements
		RelationshipRepositoryInformation {

	private ResourceInformation sourceResourceInformation;

	public RelationshipRepositoryInformationImpl(Class<?> repositoryClass,ResourceInformation sourceResourceInformation,
			ResourceInformation targetResourceInformation) {
		super(repositoryClass, targetResourceInformation);
		this.sourceResourceInformation = sourceResourceInformation;
	}

	@Override
	public ResourceInformation getSourceResourceInformation() {
		return sourceResourceInformation;
	}
}
