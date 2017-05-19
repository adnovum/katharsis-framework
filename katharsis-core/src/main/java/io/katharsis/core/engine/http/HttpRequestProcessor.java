package io.katharsis.core.engine.http;

import java.io.IOException;

public interface HttpRequestProcessor {

	void process(HttpRequestContext context) throws IOException;
}
