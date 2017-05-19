package io.katharsis.core.module;

import java.util.Arrays;
import java.util.List;

import io.katharsis.core.engine.internal.information.resource.ResourceFieldImpl;
import io.katharsis.core.engine.information.resource.ResourceField;
import io.katharsis.core.engine.information.resource.ResourceFieldType;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilderContext;
import io.katharsis.core.engine.parser.TypeParser;

public class TestResourceInformationBuilder implements ResourceInformationBuilder {

	private ResourceInformationBuilderContext context;

	@Override
	public boolean accept(Class<?> resourceClass) {
		return resourceClass == TestResource.class;
	}

	@Override
	public ResourceInformation build(Class<?> resourceClass) {
		ResourceField idField = new ResourceFieldImpl("testId", "id", ResourceFieldType.ID, Integer.class, null, null);
		List<ResourceField> fields = Arrays.asList(idField);
		TypeParser typeParser = context.getTypeParser();
		ResourceInformation info = new ResourceInformation(typeParser, resourceClass, resourceClass.getSimpleName(), null, fields);
		return info;
	}

	@Override
	public String getResourceType(Class<?> clazz) {
		return "testId";
	}

	@Override
	public void init(ResourceInformationBuilderContext context) {
		this.context = context;
	}

}