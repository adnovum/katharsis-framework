package io.katharsis.legacy.queryParams.context;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.registry.ResourceRegistry;

/**
 * @deprecated make use of QuerySpec
 */
@Deprecated
public abstract class AbstractQueryParamsParserContext implements QueryParamsParserContext {

	private final ResourceInformation resourceInformation;

	protected AbstractQueryParamsParserContext(ResourceRegistry resourceRegistry, JsonPath path) {
		resourceInformation = resourceRegistry.getEntry(path.getResourceName()).getResourceInformation();
	}

	@Override
	public ResourceInformation getRequestedResourceInformation() {
		return resourceInformation;
	}
}
