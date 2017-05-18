package io.katharsis.module.http;

import java.io.IOException;

public interface HttpRequestContext extends HttpRequestContextBase {

	boolean accepts(String contentType);

	void setContentType(String contentType);

	void setResponse(int statusCode, String text) throws IOException;

	boolean acceptsAny();
}
