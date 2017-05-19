package io.katharsis.core.queryspec;

import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.information.resource.ResourceInformation;

public interface QuerySpecDeserializer {

	void init(QuerySpecDeserializerContext ctx);

	QuerySpec deserialize(ResourceInformation resourceInformation, Map<String, Set<String>> queryParams);
}
