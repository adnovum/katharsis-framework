/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.katharsis.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.katharsis.core.boot.KatharsisBoot;
import io.katharsis.core.engine.http.HttpRequestContextProvider;
import io.katharsis.core.engine.dispatcher.RequestDispatcher;
import io.katharsis.servlet.internal.FilterPropertiesProvider;
import io.katharsis.servlet.internal.ServletModule;
import io.katharsis.servlet.internal.ServletRequestContext;

/**
 * Servlet filter class to integrate with Katharsis.
 * <p>
 * <p>
 * Child class can override {@link #initKatharsis(KatharsisBoot)} method and make use of KatharsisBoot for further customizations.
 * </p>
 */
public class KatharsisFilter implements Filter {

	protected KatharsisBoot boot;

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

		boot = new KatharsisBoot();
		boot.setPropertiesProvider(new FilterPropertiesProvider(filterConfig));

		HttpRequestContextProvider provider = (HttpRequestContextProvider) boot.getDefaultServiceUrlProvider();
		boot.addModule(new ServletModule(provider));
		initKatharsis(boot);
		boot.boot();
	}

	protected void initKatharsis(KatharsisBoot boot) {
		// nothing to do here
	}

	@Override
	public void destroy() {
		// nothing to do
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
			ServletContext servletContext = filterConfig.getServletContext();
			ServletRequestContext context = new ServletRequestContext(servletContext, (HttpServletRequest) req,
					(HttpServletResponse) res, boot.getWebPathPrefix());
			RequestDispatcher requestDispatcher = boot.getRequestDispatcher();
			requestDispatcher.process(context);
			if (!context.checkAbort()) {
				chain.doFilter(req, res);
			}
		}
		else {
			chain.doFilter(req, res);
		}
	}

}
