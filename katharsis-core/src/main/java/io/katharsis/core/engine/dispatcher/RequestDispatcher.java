package io.katharsis.core.engine.dispatcher;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.http.HttpRequestContextBase;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.document.Document;

public interface RequestDispatcher {

	void process(HttpRequestContextBase requestContextBase) throws IOException;

	Response dispatchRequest(String jsonPath, String method, Map<String, Set<String>> parameters,
			RepositoryMethodParameterProvider parameterProvider,
			Document requestBody);

	void dispatchAction(String jsonPath, String method, Map<String, Set<String>> parameters);
}
