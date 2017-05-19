package io.katharsis.core.resource.internal;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.properties.PropertiesProvider;
import io.katharsis.core.engine.internal.jackson.JsonApiModuleBuilder;
import io.katharsis.core.queryspec.internal.QuerySpecAdapter;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.legacy.internal.QueryParamsAdapter;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.legacy.registry.ResourceRegistryBuilder;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.mock.repository.MockRepositoryUtil;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.resource.registry.ResourceRegistryBuilderTest;
import io.katharsis.core.resource.registry.ResourceRegistryTest;

public class AbstractDocumentMapperTest {

	protected DocumentMapper mapper;
	protected ResourceRegistry resourceRegistry;
	protected ObjectMapper objectMapper;

	@Before
	public void setup() {
		MockRepositoryUtil.clear();

		ResourceInformationBuilder resourceInformationBuilder = new AnnotationResourceInformationBuilder(new ResourceFieldNameTransformer());
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(moduleRegistry, new SampleJsonServiceLocator(), resourceInformationBuilder);
		resourceRegistry = registryBuilder.build(ResourceRegistryBuilderTest.TEST_MODELS_PACKAGE, moduleRegistry, new ConstantServiceUrlProvider(ResourceRegistryTest.TEST_MODELS_URL));

		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JsonApiModuleBuilder().build(resourceRegistry, false));

		mapper = new DocumentMapper(resourceRegistry, objectMapper, getPropertiesProvider());
	}

	protected PropertiesProvider getPropertiesProvider() {
		return null;
	}

	@After
	public void tearDown() {
		MockRepositoryUtil.clear();
	}

	protected QueryAdapter createAdapter() {
		return new QueryParamsAdapter(new QueryParams());
	}

	protected QueryAdapter toAdapter(QuerySpec querySpec) {
		return new QuerySpecAdapter(querySpec, resourceRegistry);
	}

	protected JsonApiResponse toResponse(Object entity) {
		JsonApiResponse response = new JsonApiResponse();
		response.setEntity(entity);
		return response;
	}

}
