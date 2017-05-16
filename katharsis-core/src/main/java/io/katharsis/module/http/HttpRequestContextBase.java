package io.katharsis.module.http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;

public interface HttpRequestContextBase {

	RepositoryMethodParameterProvider getRequestParameterProvider();

	String getRequestHeader(String name);

	Map<String, Set<String>> getRequestParameters();

	String getPath();

	String getBaseUrl();

	byte[] getRequestBody() throws IOException;

	void setResponseHeader(String name, String value);

	void setResponse(int code, byte[] body) throws IOException;

	String getMethod();

	String getResponseHeader(String name);
}
