package io.katharsis.core.engine.internal.http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.internal.dispatcher.path.ActionPath;
import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathBuilder;
import io.katharsis.core.engine.internal.exception.KatharsisExceptionMapper;
import io.katharsis.core.exception.KatharsisMappableException;
import io.katharsis.core.exception.KatharsisMatchingException;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.module.Module;
import io.katharsis.core.engine.http.HttpHeaders;
import io.katharsis.core.engine.http.HttpRequestContext;
import io.katharsis.core.engine.http.HttpRequestContextProvider;
import io.katharsis.core.engine.dispatcher.RequestDispatcher;
import io.katharsis.core.engine.http.HttpRequestProcessor;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.url.ServiceUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonApiRequestProcessor implements HttpRequestProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonApiRequestProcessor.class);

	public static final String JSONAPI_CONTENT_TYPE = "application/vnd.api+json";

	private Module.ModuleContext moduleContext;

	public JsonApiRequestProcessor(Module.ModuleContext moduleContext) {
		this.moduleContext = moduleContext;
	}

	public static boolean isJsonApiRequest(HttpRequestContext requestContext) {
		if (requestContext.getMethod().equalsIgnoreCase(HttpMethod.PATCH.toString()) || requestContext.getMethod()
				.equalsIgnoreCase(HttpMethod.POST.toString())) {
			String contentType = requestContext.getRequestHeader(HttpHeaders.HTTP_CONTENT_TYPE);
			if (contentType == null || !contentType.startsWith(JSONAPI_CONTENT_TYPE)) {
				return false;
			}
		}
		boolean acceptsJsonApi = requestContext.accepts(JSONAPI_CONTENT_TYPE);
		boolean acceptsAny = requestContext.acceptsAny();
		return acceptsJsonApi || acceptsAny;
	}

	@Override
	public void process(HttpRequestContext requestContext) throws IOException {
		if (isJsonApiRequest(requestContext)) {

			ResourceRegistry resourceRegistry = moduleContext.getResourceRegistry();
			RequestDispatcher requestDispatcher = moduleContext.getRequestDispatcher();

			ServiceUrlProvider serviceUrlProvider = resourceRegistry.getServiceUrlProvider();
			try {
				String path = requestContext.getPath();

				if (serviceUrlProvider instanceof HttpRequestContextProvider) {
					((HttpRequestContextProvider) serviceUrlProvider).onRequestStarted(requestContext);
				}

				JsonPath jsonPath = new PathBuilder(resourceRegistry).build(path);
				Map<String, Set<String>> parameters = requestContext.getRequestParameters();
				String method = requestContext.getMethod();

				if (jsonPath instanceof ActionPath) {
					// inital implementation, has to improve
					requestDispatcher.dispatchAction(path, method, parameters);
				}
				else if (jsonPath != null) {
					byte[] requestBody = requestContext.getRequestBody();

					Document document = null;
					if (requestBody != null && requestBody.length > 0) {
						ObjectMapper objectMapper = moduleContext.getObjectMapper();
						document = objectMapper.readerFor(Document.class).readValue(requestBody);
					}

					RepositoryMethodParameterProvider parameterProvider = requestContext.getRequestParameterProvider();
					Response katharsisResponse = requestDispatcher
							.dispatchRequest(path, method, parameters, parameterProvider, document);
					setResponse(requestContext, katharsisResponse);
				}
				else {
					// no repositories invoked, we do nothing
				}

			}
			catch (KatharsisMappableException e) {
				// log error in KatharsisMappableException mapper.
				Response
						katharsisResponse = new KatharsisExceptionMapper().toErrorResponse(e).toResponse();
				setResponse(requestContext, katharsisResponse);
			}
			catch (KatharsisMatchingException e) {
				LOGGER.warn("failed to process request", e);
			}

		}
	}

	private void setResponse(HttpRequestContext requestContext, Response katharsisResponse)
			throws IOException {
		if (katharsisResponse != null) {
			ObjectMapper objectMapper = moduleContext.getObjectMapper();
			String responseBody = objectMapper.writeValueAsString(katharsisResponse.getDocument());

			requestContext.setResponse(katharsisResponse.getHttpStatus(), responseBody);

			String contentType = JSONAPI_CONTENT_TYPE;
			requestContext.setResponseHeader("Content-Type", contentType);
		}
	}
}
