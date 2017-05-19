package io.katharsis.servlet.internal;

import io.katharsis.core.module.Module;
import io.katharsis.core.engine.http.HttpRequestContextProvider;

public class ServletModule implements Module {


	private HttpRequestContextProvider contextProvider;

	public ServletModule(HttpRequestContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public void setupModule(ModuleContext context) {
		context.addSecurityProvider(new ServletSecurityProvider(contextProvider));
	}

	@Override
	public String getModuleName() {
		return "servlet";
	}
}
