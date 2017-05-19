package io.katharsis.core.engine.information.repository;

public interface RepositoryAction {

	public enum RepositoryActionType {
		REPOSITORY,
		RESOURCE
	}

	public String getName();

	/**
	 * @return whether a document or document action
	 */
	public RepositoryActionType getActionType();
}
