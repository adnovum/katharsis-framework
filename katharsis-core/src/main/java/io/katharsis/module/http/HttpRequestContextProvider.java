package io.katharsis.module.http;

import io.katharsis.resource.registry.ServiceUrlProvider;

public class HttpRequestContextProvider implements ServiceUrlProvider {


	private ThreadLocal<HttpRequestContext> threadLocal = new ThreadLocal<>();

	public HttpRequestContext getRequestContext() {
		return threadLocal.get();
	}

	@Override
	public String getUrl() {
		HttpRequestContext request = threadLocal.get();
		if (request == null) {
			throw new IllegalStateException("uriInfo not available, make sure to call onRequestStarted in advance");
		}
		return request.getBaseUrl();
	}

	public void onRequestStarted(HttpRequestContext request) {
		threadLocal.set(request);
	}

	public void onRequestFinished() {
		threadLocal.remove();
	}
}
