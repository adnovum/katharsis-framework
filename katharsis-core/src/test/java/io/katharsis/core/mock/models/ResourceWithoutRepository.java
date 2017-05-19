package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.JsonApiToOne;

@JsonApiResource(type = "resourceWithoutRepository")
public class ResourceWithoutRepository {

    @JsonApiId
    private String id;

    @JsonApiToOne
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
