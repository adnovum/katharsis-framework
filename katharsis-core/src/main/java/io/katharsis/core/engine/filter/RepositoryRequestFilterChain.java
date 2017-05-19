package io.katharsis.core.engine.filter;

import io.katharsis.core.repository.response.JsonApiResponse;

/**
 * Manages the chain of filters and their application to a request.
 */
public interface RepositoryRequestFilterChain {

	/**
	 * Invokes the next filter in the chain or the actual document once all filters
	 * have been invoked.
	 *
	 * @param filterRequestContext request context
	 */
	public JsonApiResponse doFilter(RepositoryFilterContext context);

}
