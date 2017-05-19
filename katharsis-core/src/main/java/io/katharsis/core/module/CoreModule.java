package io.katharsis.core.module;

import io.katharsis.core.engine.internal.http.JsonApiRequestProcessor;
import io.katharsis.core.engine.internal.exception.DefaultExceptionMapperLookup;
import io.katharsis.legacy.repository.information.DefaultRelationshipRepositoryInformationBuilder;
import io.katharsis.legacy.repository.information.DefaultResourceRepositoryInformationBuilder;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.module.discovery.DefaultResourceLookup;

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
