package io.katharsis.servlet.internal;

import io.katharsis.core.engine.http.HttpRequestContextProvider;
import io.katharsis.core.engine.security.SecurityProvider;

public class ServletSecurityProvider implements SecurityProvider {

	private HttpRequestContextProvider contextProvider;

	public ServletSecurityProvider(HttpRequestContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public boolean isUserInRole(String role) {
		ServletRequestContext request = (ServletRequestContext) contextProvider.getRequestContext();
		return request.getRequest().isUserInRole(role);
	}

}
