package io.katharsis.module.http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.repository.response.Response;
import io.katharsis.resource.Document;

public interface HttpRequestDispatcher {

	void process(HttpRequestContextBase requestContextBase) throws IOException;

	Response dispatchRequest(String jsonPath, String method, Map<String, Set<String>> parameters,
			RepositoryMethodParameterProvider parameterProvider,
			Document requestBody);

	void dispatchAction(String jsonPath, String method, Map<String, Set<String>> parameters);
}
