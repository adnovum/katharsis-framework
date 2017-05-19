package io.katharsis.client.action;

import io.katharsis.client.http.HttpAdapter;
import io.katharsis.core.engine.url.ServiceUrlProvider;

public interface ActionStubFactoryContext {

	ServiceUrlProvider getServiceUrlProvider();

	HttpAdapter getHttpAdapter();

}
