package io.katharsis.module;

import io.katharsis.core.internal.dispatcher.http.JsonApiRequestProcessor;
import io.katharsis.core.internal.exception.DefaultExceptionMapperLookup;
import io.katharsis.core.internal.repository.information.DefaultRelationshipRepositoryInformationBuilder;
import io.katharsis.core.internal.repository.information.DefaultResourceRepositoryInformationBuilder;
import io.katharsis.core.internal.resource.AnnotationResourceInformationBuilder;
import io.katharsis.resource.information.ResourceFieldNameTransformer;
import io.katharsis.resource.registry.DefaultResourceLookup;

/**
 * Register the Katharsis core feature set as module.
 */
@Deprecated
public class CoreModule extends SimpleModule {

	public static final String MODULE_NAME = "core";

	public CoreModule(String resourceSearchPackage, ResourceFieldNameTransformer resourceFieldNameTransformer) {
		this(resourceFieldNameTransformer);
		this.addResourceLookup(new DefaultResourceLookup(resourceSearchPackage));
		this.addExceptionMapperLookup(new DefaultExceptionMapperLookup(resourceSearchPackage));
	}

	public CoreModule(ResourceFieldNameTransformer resourceFieldNameTransformer) {
		super(MODULE_NAME);
		this.addResourceInformationBuilder(new AnnotationResourceInformationBuilder(resourceFieldNameTransformer));
		this.addRepositoryInformationBuilder(new DefaultResourceRepositoryInformationBuilder());
		this.addRepositoryInformationBuilder(new DefaultRelationshipRepositoryInformationBuilder());
	}

	@Override
	public void setupModule(ModuleContext context) {
		this.addHttpRequestProcessor(new JsonApiRequestProcessor(context));
		super.setupModule(context);
	}
}
