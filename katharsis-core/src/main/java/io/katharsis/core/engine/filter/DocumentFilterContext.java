package io.katharsis.core.engine.filter;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.document.Document;

/**
 * Provides request information to {@link DocumentFilter}.
 */
public interface DocumentFilterContext {

	Document getRequestBody();

	RepositoryMethodParameterProvider getParameterProvider();

	QueryParams getQueryParams();

	JsonPath getJsonPath();

	QueryAdapter getQueryAdapter();

	String getMethod();

}
