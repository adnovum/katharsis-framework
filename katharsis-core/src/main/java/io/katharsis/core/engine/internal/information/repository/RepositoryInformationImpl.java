package io.katharsis.core.engine.internal.information.repository;

import io.katharsis.core.engine.information.repository.RepositoryInformation;
import io.katharsis.core.engine.information.resource.ResourceInformation;

abstract class RepositoryInformationImpl implements RepositoryInformation {

	private ResourceInformation resourceInformation;

	private Class<?> repositoryClass;

	public RepositoryInformationImpl(Class<?> repositoryClass, ResourceInformation resourceInformation) {
		super();
		this.repositoryClass = repositoryClass;
		this.resourceInformation = resourceInformation;
	}

	@Override
	public Class<?> getRepositoryClass() {
		return repositoryClass;
	}

	@Override
	public ResourceInformation getResourceInformation() {
		return resourceInformation;
	}
}