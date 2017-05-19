package io.katharsis.core.engine.internal.jackson.mock.models;

import java.util.List;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.JsonApiToMany;

@JsonApiResource(type = "classCs")
public class ClassC {

    @JsonApiId
    private Long id;

    @JsonApiToMany
    private List<ClassA> classAs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ClassA> getClassAs() {
        return classAs;
    }

    public void setClassAs(List<ClassA> classAs) {
        this.classAs = classAs;
    }
}
