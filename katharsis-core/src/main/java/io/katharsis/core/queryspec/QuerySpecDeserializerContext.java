package io.katharsis.core.queryspec;

import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

public interface QuerySpecDeserializerContext {

	public ResourceRegistry getResourceRegistry();

	public TypeParser getTypeParser();
}
