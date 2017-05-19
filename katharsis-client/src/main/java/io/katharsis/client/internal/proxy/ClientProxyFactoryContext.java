package io.katharsis.client.internal.proxy;

import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.resource.list.DefaultResourceList;

public interface ClientProxyFactoryContext {

	ModuleRegistry getModuleRegistry();

	<T> DefaultResourceList<T> getCollection(Class<T> resourceClass, String url);

}
