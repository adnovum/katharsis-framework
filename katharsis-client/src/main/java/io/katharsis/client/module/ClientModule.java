package io.katharsis.client.module;

import io.katharsis.legacy.repository.information.DefaultRelationshipRepositoryInformationBuilder;
import io.katharsis.legacy.repository.information.DefaultResourceRepositoryInformationBuilder;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.module.Module;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;

public class ClientModule implements Module {

	@Override
	public String getModuleName() {
		return "client";
	}

	@Override
	public void setupModule(ModuleContext context) {
		context.addResourceInformationBuilder(new AnnotationResourceInformationBuilder(new ResourceFieldNameTransformer()));
		context.addRepositoryInformationBuilder(new DefaultResourceRepositoryInformationBuilder());
		context.addRepositoryInformationBuilder(new DefaultRelationshipRepositoryInformationBuilder());
	}

}
