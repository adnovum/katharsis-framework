package io.katharsis.core.module;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "test")
public class TestResource {

	@JsonApiId
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}