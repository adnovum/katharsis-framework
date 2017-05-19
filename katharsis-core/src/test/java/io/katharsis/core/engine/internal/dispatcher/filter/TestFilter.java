package io.katharsis.core.engine.internal.dispatcher.filter;

import io.katharsis.core.engine.filter.DocumentFilter;
import io.katharsis.core.engine.filter.DocumentFilterChain;
import io.katharsis.core.engine.filter.DocumentFilterContext;
import io.katharsis.core.engine.dispatcher.Response;

public class TestFilter implements DocumentFilter {

	@Override
	public Response filter(DocumentFilterContext filterRequestContext, DocumentFilterChain chain) {
		return chain.doFilter(filterRequestContext);
	}
}
