package io.katharsis.core.internal.dispatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.katharsis.core.internal.dispatcher.controller.BaseController;
import io.katharsis.core.internal.dispatcher.path.JsonPath;
import io.katharsis.core.internal.dispatcher.path.PathBuilder;
import io.katharsis.core.internal.exception.ExceptionMapperRegistry;
import io.katharsis.core.internal.query.QueryAdapterBuilder;
import io.katharsis.errorhandling.exception.RepositoryNotFoundException;
import io.katharsis.errorhandling.exception.ResourceFieldNotFoundException;
import io.katharsis.errorhandling.mapper.JsonApiExceptionMapper;
import io.katharsis.legacy.internal.QueryParamsAdapter;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.module.ModuleRegistry;
import io.katharsis.module.http.HttpRequestContextBase;
import io.katharsis.module.http.HttpRequestDispatcher;
import io.katharsis.module.http.HttpRequestProcessor;
import io.katharsis.module.http.HttpRequestContextProvider;
import io.katharsis.repository.filter.DocumentFilter;
import io.katharsis.repository.filter.DocumentFilterChain;
import io.katharsis.repository.filter.DocumentFilterContext;
import io.katharsis.repository.request.QueryAdapter;
import io.katharsis.repository.response.Response;
import io.katharsis.resource.Document;
import io.katharsis.resource.information.ResourceField;
import io.katharsis.resource.information.ResourceInformation;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ServiceUrlProvider;
import io.katharsis.utils.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that can be used to integrate Katharsis with external frameworks like Jersey, Spring etc. See katharsis-rs
 * and katharsis-servlet for usage.
 */
public class RequestDispatcherImpl implements HttpRequestDispatcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final ControllerRegistry controllerRegistry;

	private final ExceptionMapperRegistry exceptionMapperRegistry;

	private ModuleRegistry moduleRegistry;

	private QueryAdapterBuilder queryAdapterBuilder;

	public RequestDispatcherImpl(ModuleRegistry moduleRegistry, ControllerRegistry controllerRegistry,
			ExceptionMapperRegistry exceptionMapperRegistry, QueryAdapterBuilder queryAdapterBuilder) {
		this.controllerRegistry = controllerRegistry;
		this.moduleRegistry = moduleRegistry;
		this.exceptionMapperRegistry = exceptionMapperRegistry;
		this.queryAdapterBuilder = queryAdapterBuilder;

		// TODO clean this class up
		this.moduleRegistry.setRequestDispatcher(this);
	}

	@Override
	public void process(HttpRequestContextBase requestContextBase) throws IOException {
		HttpRequestContextBaseAdapter requestContext = new HttpRequestContextBaseAdapter(requestContextBase);
		ResourceRegistry resourceRegistry = moduleRegistry.getResourceRegistry();
		ServiceUrlProvider serviceUrlProvider = resourceRegistry.getServiceUrlProvider();
		try {
			if (serviceUrlProvider instanceof HttpRequestContextProvider) {
				((HttpRequestContextProvider) serviceUrlProvider).onRequestStarted(requestContext);
			}

			List<HttpRequestProcessor> processors = moduleRegistry.getHttpRequestProcessors();
			if(processors.isEmpty()){
				throw new IllegalStateException("no processors available");
			}
			for (HttpRequestProcessor processor : processors) {
				processor.process(requestContext);
				if (requestContext.hasResponse()) {
					break;
				}
			}
		}
		finally {
			if (serviceUrlProvider instanceof HttpRequestContextProvider) {
				((HttpRequestContextProvider) serviceUrlProvider).onRequestFinished();
			}
		}
	}

	/**
	 * Dispatch the request from a client
	 *
	 * @param path built represents the URI sent in the request
	 * @param method type of the request e.g. POST, GET, PATCH
	 * @param parameterProvider repository method parameter provider
	 * @param requestBody deserialized body of the client request
	 * @return the response form the Katharsis
	 */
	@Override
	public Response dispatchRequest(String path, String method, Map<String, Set<String>> parameters,
			RepositoryMethodParameterProvider parameterProvider,
			Document requestBody) {

		JsonPath jsonPath = new PathBuilder(moduleRegistry.getResourceRegistry()).build(path);
		try {
			BaseController controller = controllerRegistry.getController(jsonPath, method);

			ResourceInformation resourceInformation = getRequestedResource(jsonPath);
			QueryAdapter queryAdapter = queryAdapterBuilder.build(resourceInformation, parameters);

			DefaultFilterRequestContext context = new DefaultFilterRequestContext(jsonPath, queryAdapter, parameterProvider,
					requestBody, method);
			DefaultFilterChain chain = new DefaultFilterChain(controller);
			return chain.doFilter(context);
		}
		catch (Exception e) {
			Optional<JsonApiExceptionMapper> exceptionMapper = exceptionMapperRegistry.findMapperFor(e.getClass());
			if (exceptionMapper.isPresent()) {
				//noinspection unchecked
				return exceptionMapper.get().toErrorResponse(e).toResponse();
			}
			else {
				logger.error("failed to process request", e);
				throw e;
			}
		}
	}

	private ResourceInformation getRequestedResource(JsonPath jsonPath) {
		ResourceRegistry resourceRegistry = moduleRegistry.getResourceRegistry();
		RegistryEntry registryEntry = resourceRegistry.getEntry(jsonPath.getResourceName());
		if (registryEntry == null) {
			throw new RepositoryNotFoundException(jsonPath.getResourceName());
		}
		String elementName = jsonPath.getElementName();
		if (elementName != null && !elementName.equals(jsonPath.getResourceName())) {
			ResourceField relationshipField = registryEntry.getResourceInformation().findRelationshipFieldByName(elementName);
			if (relationshipField == null) {
				throw new ResourceFieldNotFoundException(elementName);
			}
			String oppositeResourceType = relationshipField.getOppositeResourceType();
			return resourceRegistry.getEntry(oppositeResourceType).getResourceInformation();
		}
		else {
			return registryEntry.getResourceInformation();
		}
	}

	class DefaultFilterChain implements DocumentFilterChain {

		protected int filterIndex = 0;

		protected BaseController controller;

		public DefaultFilterChain(BaseController controller) {
			this.controller = controller;
		}

		@Override
		public Response doFilter(DocumentFilterContext context) {
			List<DocumentFilter> filters = moduleRegistry.getFilters();
			if (filterIndex == filters.size()) {
				return controller.handle(context.getJsonPath(), context.getQueryAdapter(), context.getParameterProvider(),
						context.getRequestBody());
			}
			else {
				DocumentFilter filter = filters.get(filterIndex);
				filterIndex++;
				return filter.filter(context, this);
			}
		}
	}

	class ActionFilterChain implements DocumentFilterChain {

		protected int filterIndex = 0;


		@Override
		public Response doFilter(DocumentFilterContext context) {
			List<DocumentFilter> filters = moduleRegistry.getFilters();
			if (filterIndex == filters.size()) {
				return null;
			}
			else {
				DocumentFilter filter = filters.get(filterIndex);
				filterIndex++;
				return filter.filter(context, this);
			}
		}
	}

	@Override
	public void dispatchAction(String path, String method, Map<String, Set<String>> parameters) {
		JsonPath jsonPath = new PathBuilder(moduleRegistry.getResourceRegistry()).build(path);

		// preliminary implementation, more to come in the future
		ActionFilterChain chain = new ActionFilterChain();

		DefaultFilterRequestContext context = new DefaultFilterRequestContext(jsonPath, null, null, null, method);
		chain.doFilter(context);
	}

	class DefaultFilterRequestContext implements DocumentFilterContext {

		protected JsonPath jsonPath;

		protected QueryAdapter queryAdapter;

		protected RepositoryMethodParameterProvider parameterProvider;

		protected Document requestBody;

		private String method;

		public DefaultFilterRequestContext(JsonPath jsonPath, QueryAdapter queryAdapter,
				RepositoryMethodParameterProvider parameterProvider, Document requestBody, String method) {
			this.jsonPath = jsonPath;
			this.queryAdapter = queryAdapter;
			this.parameterProvider = parameterProvider;
			this.requestBody = requestBody;
			this.method = method;
		}

		@Override
		public Document getRequestBody() {
			return requestBody;
		}

		@Override
		public RepositoryMethodParameterProvider getParameterProvider() {
			return parameterProvider;
		}

		@Override
		public QueryParams getQueryParams() {
			return ((QueryParamsAdapter) queryAdapter).getQueryParams();
		}

		@Override
		public QueryAdapter getQueryAdapter() {
			return queryAdapter;
		}

		@Override
		public JsonPath getJsonPath() {
			return jsonPath;
		}

		@Override
		public String getMethod() {
			return method;
		}
	}

	public QueryAdapterBuilder getQueryAdapterBuilder() {
		return queryAdapterBuilder;
	}
}
