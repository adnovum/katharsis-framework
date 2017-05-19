package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "schedules")
public class Schedule {

	@JsonApiId
	private Long id;

	private String name;
	
	public Long getId() {
		return id;
	}

	public Schedule setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
