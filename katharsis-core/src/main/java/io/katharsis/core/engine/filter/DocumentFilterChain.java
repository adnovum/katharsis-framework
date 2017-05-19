package io.katharsis.core.engine.filter;

import io.katharsis.core.engine.internal.dispatcher.controller.BaseController;
import io.katharsis.core.engine.dispatcher.Response;

/**
 * Manages the chain of filters and their application to a request.
 */
public interface DocumentFilterChain {

	/**
	 * Executes the next filter in the request chain or the actual {@link BaseController} once all filters
	 * have been invoked.
	 *
	 * @param filterRequestContext request context
	 * @return new execution context
	 */
	Response doFilter(DocumentFilterContext filterRequestContext);
}
