package io.katharsis.core.engine.filter;

/**
 * Manages the chain of document filters to perform result filtering.
 */
public interface RepositoryResultFilterChain<T> {

	/**
	 * Invokes the next filter in the chain or the actual document once all filters
	 * have been invoked.
	 *
	 * @param context holding the request and other information.
	 * @return filtered result
	 */
	public Iterable<T> doFilter(RepositoryFilterContext context);

}
