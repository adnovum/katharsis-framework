package io.katharsis.core.mock.models;

import java.util.List;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.JsonApiToMany;
import io.katharsis.core.resource.annotations.JsonApiToOne;


@JsonApiResource(type = "projects-polymorphic")
public class ProjectPolymorphic {

    @JsonApiId
    private Long id;
    @JsonApiToOne
    private Object task;
    @JsonApiToMany
    private List<Object> tasks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getTask() {
        return task;
    }

    public void setTask(Object task) {
        this.task = task;
    }

    public List<Object> getTasks() {
        return tasks;
    }

    public void setTasks(List<Object> tasks) {
        this.tasks = tasks;
    }
}