package io.katharsis.test.mock.models;

import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "tasksSubType")
public class TaskSubType extends Task {

	private int subTypeValue;

	public int getSubTypeValue() {
		return subTypeValue;
	}

	public void setSubTypeValue(int subTypeValue) {
		this.subTypeValue = subTypeValue;
	}
}
