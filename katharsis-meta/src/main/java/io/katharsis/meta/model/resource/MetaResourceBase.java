package io.katharsis.meta.model.resource;

import java.util.List;

import io.katharsis.core.resource.annotations.JsonApiResource;

/**
 * Base class for resource classes. Such objects have the same
 * structure (attributes, relationships, etc.) as resources, but
 * are not resources by themselves. They do not carry a resourceType.
 */
@JsonApiResource(type = "meta/resourceBase")
public class MetaResourceBase extends MetaJsonObject {

	@Override
	public List<MetaResourceField> getDeclaredAttributes() {
		return (List<MetaResourceField>) super.getDeclaredAttributes();
	}
}
