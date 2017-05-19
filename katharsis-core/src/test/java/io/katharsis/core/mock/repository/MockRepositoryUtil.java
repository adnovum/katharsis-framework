package io.katharsis.core.mock.repository;

import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.resource.registry.ResourceRegistryBuilderTest;
import io.katharsis.legacy.locator.JsonServiceLocator;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.legacy.registry.ResourceRegistryBuilder;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.core.module.discovery.DefaultResourceLookup;
import io.katharsis.core.engine.registry.ResourceRegistry;

public class MockRepositoryUtil {

	public static void clear() {
		TaskRepository.clear();
		ProjectRepository.clear();
		TaskToProjectRepository.clear();
		HierarchicalTaskRepository.clear();
		ScheduleRepositoryImpl.clear();
	}

	public static ResourceRegistry setupResourceRegistry() {
		JsonServiceLocator jsonServiceLocator = new SampleJsonServiceLocator();
		ResourceInformationBuilder resourceInformationBuilder = new AnnotationResourceInformationBuilder(new ResourceFieldNameTransformer());
		ModuleRegistry moduleRegistry = new ModuleRegistry();
		ResourceRegistryBuilder resourceRegistryBuilder = new ResourceRegistryBuilder(moduleRegistry, jsonServiceLocator, resourceInformationBuilder);
		DefaultResourceLookup resourceLookup = newResourceLookup();
		return resourceRegistryBuilder.build(resourceLookup, moduleRegistry, new ConstantServiceUrlProvider("http://127.0.0.1"));
	}

	public static DefaultResourceLookup newResourceLookup() {
		return new DefaultResourceLookup(ResourceRegistryBuilderTest.TEST_MODELS_PACKAGE);
	}

}
