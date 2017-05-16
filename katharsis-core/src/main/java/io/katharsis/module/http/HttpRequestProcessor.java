package io.katharsis.module.http;

import java.io.IOException;

public interface HttpRequestProcessor {

	void process(HttpRequestContext context) throws IOException;
}
