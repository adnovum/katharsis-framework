package io.katharsis.core.engine.internal.dispatcher.registry;

import static io.katharsis.core.resource.registry.ResourceRegistryTest.TEST_MODELS_URL;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.katharsis.core.engine.internal.dispatcher.ControllerRegistry;
import io.katharsis.core.engine.internal.dispatcher.path.PathBuilder;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.exception.MethodNotFoundException;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.legacy.registry.ResourceRegistryBuilder;
import io.katharsis.core.module.ModuleRegistry;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.resource.registry.ResourceRegistryBuilderTest;

public class ControllerRegistryTest {

    private ResourceRegistry resourceRegistry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void prepare() {
        ResourceInformationBuilder resourceInformationBuilder = new AnnotationResourceInformationBuilder(
            new ResourceFieldNameTransformer());
        ModuleRegistry moduleRegistry = new ModuleRegistry();
        ResourceRegistryBuilder registryBuilder = new ResourceRegistryBuilder(moduleRegistry, new SampleJsonServiceLocator(),
            resourceInformationBuilder);
        resourceRegistry = registryBuilder
            .build(ResourceRegistryBuilderTest.TEST_MODELS_PACKAGE,  moduleRegistry, new ConstantServiceUrlProvider(TEST_MODELS_URL));
    }

    @Test
    public void onUnsupportedRequestRegisterShouldThrowError() {
        // GIVEN
        PathBuilder pathBuilder = new PathBuilder(resourceRegistry);
        JsonPath jsonPath = pathBuilder.buildPath("/tasks/");
        String requestType = "PATCH";
        ControllerRegistry sut = new ControllerRegistry(null);

        // THEN
        expectedException.expect(MethodNotFoundException.class);

        // WHEN
        sut.getController(jsonPath, requestType);
    }
}
