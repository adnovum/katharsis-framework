package io.katharsis.core.engine.query;

import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.information.resource.ResourceInformation;

/**
 * Builds the query adapter for the given parameters, resulting in either a queryParams or querySpec adapter depending on the chosen implementation.
 */
public interface QueryAdapterBuilder {

	QueryAdapter build(ResourceInformation resourceInformation, Map<String, Set<String>> parameters);

}
