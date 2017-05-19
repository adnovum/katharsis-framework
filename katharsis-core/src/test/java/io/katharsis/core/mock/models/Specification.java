package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "specifications")
public class Specification extends Document {
    private String designOutlines;

    private Task task;

    public String getDesignOutlines() {
        return designOutlines;
    }

    public void setDesignOutlines(String designOutlines) {
        this.designOutlines = designOutlines;
    }

    public Task getTask() {
        return task;
    }

    public Specification setTask(Task task) {
        this.task = task;
        return this;
    }
}
