package io.katharsis.core.engine.filter;

import io.katharsis.core.engine.dispatcher.RepositoryRequestSpec;

/**
 * Provides context information about Katharsis and the current request for
 * {@link RepositoryFilter}. See {@link RepositoryFilter} for a higher-level
 * filter closer to the actual repositories.
 */
public interface RepositoryFilterContext {

	/**
	 * @return information about the request
	 */
	RepositoryRequestSpec getRequest();
}
