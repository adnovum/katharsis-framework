package io.katharsis.core.engine.information.repository;

import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.engine.parser.TypeParser;

public interface RepositoryInformationBuilderContext {

	ResourceInformationBuilder getResourceInformationBuilder();

	TypeParser getTypeParser();
}
