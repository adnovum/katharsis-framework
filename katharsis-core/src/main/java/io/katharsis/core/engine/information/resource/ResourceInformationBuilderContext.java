package io.katharsis.core.engine.information.resource;

import io.katharsis.core.engine.parser.TypeParser;

public interface ResourceInformationBuilderContext {

	public String getResourceType(Class<?> clazz);
	
	public boolean accept(Class<?> type);

	public TypeParser getTypeParser();
}
