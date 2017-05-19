package io.katharsis.core.engine.filter;

import io.katharsis.core.resource.meta.MetaInformation;

/**
 * Manages the chain of document filters to resolve meta information.
 */
public interface RepositoryMetaFilterChain {

	/**
	 * Invokes the next filter in the chain or the actual document once all filters
	 * have been invoked.
	 *
	 * @param context holding the request and other information.
	 * @param resources for which to compute the meta information (as a whole, not for the individual items)
	 * @return filtered meta information
	 */
	public <T> MetaInformation doFilter(RepositoryFilterContext context, Iterable<T> resources);

}
