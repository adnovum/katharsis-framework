package io.katharsis.core.engine.internal.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.katharsis.core.engine.dispatcher.RequestDispatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.controller.CollectionGet;
import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathBuilder;
import io.katharsis.core.engine.internal.exception.ExceptionMapperRegistryTest;
import io.katharsis.core.queryspec.internal.QuerySpecAdapterBuilder;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.legacy.registry.ResourceRegistryBuilder;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.queryspec.DefaultQuerySpecDeserializer;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.http.HttpStatus;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.resource.registry.ResourceRegistryBuilderTest;
import io.katharsis.core.resource.registry.ResourceRegistryTest;

public class RequestDispatcherTest {

	private ResourceRegistry resourceRegistry;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private ModuleRegistry moduleRegistry;

	@Before
	public void prepare() {
		ResourceInformationBuilder resourceInformationBuilder = new AnnotationResourceInformationBuilder(new ResourceFieldNameTransformer());
		moduleRegistry = new ModuleRegistry();
		ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(moduleRegistry, new SampleJsonServiceLocator(), resourceInformationBuilder);
		resourceRegistry = registryBuilder.build(ResourceRegistryBuilderTest.TEST_MODELS_PACKAGE, moduleRegistry, new ConstantServiceUrlProvider(ResourceRegistryTest.TEST_MODELS_URL));

		moduleRegistry.init(new ObjectMapper());
	}

	@Test
	public void onGivenPathAndRequestTypeControllerShouldHandleRequest() throws Exception {
		// GIVEN
		String path = "/tasks/";
		String requestType = "GET";

		PathBuilder pathBuilder = new PathBuilder(resourceRegistry);
		ControllerRegistry controllerRegistry = new ControllerRegistry(null);
		CollectionGet collectionGet = mock(CollectionGet.class);
		controllerRegistry.addController(collectionGet);
		QuerySpecAdapterBuilder queryAdapterBuilder = new QuerySpecAdapterBuilder(new DefaultQuerySpecDeserializer(), moduleRegistry);
		RequestDispatcher sut = new HttpRequestProcessorImpl(moduleRegistry, controllerRegistry, null, queryAdapterBuilder);

		// WHEN
		when(collectionGet.isAcceptable(any(JsonPath.class), eq(requestType))).thenCallRealMethod();
		Map<String, Set<String>> parameters = new HashMap<>();
		sut.dispatchRequest(path, requestType, parameters, null, null);

		// THEN
		verify(collectionGet, times(1)).handle(any(JsonPath.class), any(QueryAdapter.class), any(RepositoryMethodParameterProvider.class), any(Document.class));
	}

	@Test
	public void shouldMapExceptionToErrorResponseIfMapperIsAvailable() throws Exception {

		ControllerRegistry controllerRegistry = mock(ControllerRegistry.class);
		// noinspection unchecked
		when(controllerRegistry.getController(any(JsonPath.class), anyString())).thenThrow(IllegalStateException.class);

		QuerySpecAdapterBuilder queryAdapterBuilder = new QuerySpecAdapterBuilder(new DefaultQuerySpecDeserializer(), moduleRegistry);
		RequestDispatcher
				requestDispatcher = new HttpRequestProcessorImpl(moduleRegistry, controllerRegistry, ExceptionMapperRegistryTest.exceptionMapperRegistry, queryAdapterBuilder);

		Response response = requestDispatcher.dispatchRequest("tasks", null, null, null, null);
		assertThat(response).isNotNull();

		assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);

	}

	@Test
	public void shouldThrowExceptionAsIsIfMapperIsNotAvailable() throws Exception {
		ControllerRegistry controllerRegistry = mock(ControllerRegistry.class);
		// noinspection unchecked
		when(controllerRegistry.getController(any(JsonPath.class), anyString())).thenThrow(ArithmeticException.class);

		QuerySpecAdapterBuilder queryAdapterBuilder = new QuerySpecAdapterBuilder(new DefaultQuerySpecDeserializer(), moduleRegistry);
		RequestDispatcher
				requestDispatcher = new HttpRequestProcessorImpl(moduleRegistry, controllerRegistry, ExceptionMapperRegistryTest.exceptionMapperRegistry, queryAdapterBuilder);

		expectedException.expect(ArithmeticException.class);

		Response response = requestDispatcher.dispatchRequest("tasks", null, null, null, null);
	}
}
