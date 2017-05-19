package io.katharsis.core.queryspec.internal;

import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.query.QueryAdapterBuilder;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.queryspec.QuerySpecDeserializer;
import io.katharsis.core.queryspec.QuerySpecDeserializerContext;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

public class QuerySpecAdapterBuilder implements QueryAdapterBuilder {

	private QuerySpecDeserializer querySpecDeserializer;

	private ResourceRegistry resourceRegistry;

	public QuerySpecAdapterBuilder(QuerySpecDeserializer querySpecDeserializer, final ModuleRegistry moduleRegistry) {
		this.querySpecDeserializer = querySpecDeserializer;
		this.resourceRegistry = moduleRegistry.getResourceRegistry();
		this.querySpecDeserializer.init(new QuerySpecDeserializerContext() {

			@Override
			public ResourceRegistry getResourceRegistry() {
				return resourceRegistry;
			}

			@Override
			public TypeParser getTypeParser() {
				return moduleRegistry.getTypeParser();
			}
		});
	}

	@Override
	public QueryAdapter build(ResourceInformation resourceInformation, Map<String, Set<String>> parameters) {

		return new QuerySpecAdapter(querySpecDeserializer.deserialize(resourceInformation, parameters), resourceRegistry);
	}
}
