package io.katharsis.core.engine.information.resource;

import io.katharsis.core.engine.internal.information.resource.DefaultResourceInstanceBuilder;
import io.katharsis.core.engine.document.Resource;

/**
 * Used to construct an object instance for the requested document. {@link DefaultResourceInstanceBuilder} just
 * creates new empty object instances using the default constructor. More elaborate instances may do more, 
 * like binding created entity instances to a JPA session.
 */
public interface ResourceInstanceBuilder<T> {

	/**
	 * @param body request body
	 * @return document object
	 */
	T buildResource(Resource body);
}