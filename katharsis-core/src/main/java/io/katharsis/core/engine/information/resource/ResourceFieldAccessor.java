package io.katharsis.core.engine.information.resource;

import io.katharsis.core.engine.internal.information.resource.ReflectionFieldAccessor;

/**
 * Provides access to a field of a document. See {@link ReflectionFieldAccessor}
 * for a default implementation.
 * 
 * @author Remo
 */
public interface ResourceFieldAccessor {

	public Object getValue(Object resource);

	public void setValue(Object resource, Object fieldValue);

}
