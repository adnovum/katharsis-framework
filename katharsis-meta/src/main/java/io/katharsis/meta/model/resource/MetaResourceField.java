package io.katharsis.meta.model.resource;

import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.core.resource.annotations.JsonApiResource;

/**
 * Field of a JSON API resource.
 */
@JsonApiResource(type = "meta/resourceField")
public class MetaResourceField extends MetaAttribute {

	private boolean meta;

	private boolean links;

	public boolean isMeta() {
		return meta;
	}

	public void setMeta(boolean meta) {
		this.meta = meta;
	}

	public boolean isLinks() {
		return links;
	}

	public void setLinks(boolean links) {
		this.links = links;
	}

}
