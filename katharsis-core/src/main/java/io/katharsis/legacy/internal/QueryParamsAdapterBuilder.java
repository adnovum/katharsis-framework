package io.katharsis.legacy.internal;

import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.query.QueryAdapterBuilder;
import io.katharsis.legacy.queryParams.QueryParamsBuilder;
import io.katharsis.legacy.queryParams.context.SimpleQueryParamsParserContext;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.information.resource.ResourceInformation;

public class QueryParamsAdapterBuilder implements QueryAdapterBuilder {

	private QueryParamsBuilder queryParamsBuilder;
	private ModuleRegistry moduleRegistry;

	public QueryParamsAdapterBuilder(QueryParamsBuilder queryParamsBuilder, ModuleRegistry moduleRegistry) {
		this.queryParamsBuilder = queryParamsBuilder;
		this.moduleRegistry = moduleRegistry;
	}

	@Override
	public QueryAdapter build(ResourceInformation info, Map<String, Set<String>> parameters) {
		SimpleQueryParamsParserContext context = new SimpleQueryParamsParserContext(parameters, info);
		return new QueryParamsAdapter(info, queryParamsBuilder.buildQueryParams(context), moduleRegistry);
	}
}