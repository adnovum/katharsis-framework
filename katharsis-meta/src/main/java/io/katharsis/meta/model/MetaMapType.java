package io.katharsis.meta.model;

import io.katharsis.core.resource.annotations.JsonApiRelation;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.SerializeType;

@JsonApiResource(type = "meta/mapType")
public class MetaMapType extends MetaType {

	@JsonApiRelation(serialize=SerializeType.LAZY)
	private MetaType keyType;

	public MetaType getKeyType() {
		return keyType;
	}

	public void setKeyType(MetaType keyType) {
		this.keyType = keyType;
	}

}
