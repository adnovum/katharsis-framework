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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.katharsis.core.internal.boot.KatharsisBoot;
import io.katharsis.core.internal.dispatcher.HttpRequestContextBaseAdapter;
import io.katharsis.core.internal.dispatcher.http.JsonApiRequestProcessor;
import io.katharsis.module.http.HttpRequestContextProvider;
import io.katharsis.module.http.HttpRequestDispatcher;
import io.katharsis.repository.response.HttpStatus;
import io.katharsis.servlet.internal.ServletModule;
import io.katharsis.servlet.internal.ServletPropertiesProvider;
import io.katharsis.servlet.internal.ServletRequestContext;

/**
 * Abstract base servlet class to integrate with Katharsis-core.
 * <p>
 * Child class can override {@link #initKatharsis(KatharsisBoot)} method and make use of KatharsisBoot for further customizations.
 * </p>
 */
public class KatharsisServlet extends HttpServlet {

	protected KatharsisBoot boot;

	public void init() throws ServletException {
		boot = new KatharsisBoot();
		boot.setPropertiesProvider(new ServletPropertiesProvider(getServletConfig()));

		HttpRequestContextProvider provider = (HttpRequestContextProvider) boot.getDefaultServiceUrlProvider();
		boot.addModule(new ServletModule(provider));
		initKatharsis(boot);
		boot.boot();
	}

	protected void initKatharsis(KatharsisBoot boot) {
		// nothing to do here
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletContext();

		ServletRequestContext context = new ServletRequestContext(servletContext, request, response, boot.getWebPathPrefix());
		HttpRequestDispatcher requestDispatcher = boot.getRequestDispatcher();
		requestDispatcher.process(context);

		if (!context.checkAbort()) {
			boolean jsonApiRequest = JsonApiRequestProcessor.isJsonApiRequest(new HttpRequestContextBaseAdapter(context));
			response.setStatus(jsonApiRequest ? HttpStatus.NOT_FOUND_404 : HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
		}
	}

	public KatharsisBoot getBoot() {
		return boot;
	}
}
