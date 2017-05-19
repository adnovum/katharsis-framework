package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "complexpojos")
public class ComplexPojo {

    @JsonApiId
    Long id;
    ContainedPojo containedPojo;
    String updateableProperty;

    public ComplexPojo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContainedPojo getContainedPojo() {
        return containedPojo;
    }
    public void setContainedPojo(ContainedPojo containedPojo) {
        this.containedPojo = containedPojo;
    }
    public String getUpdateableProperty() {
        return updateableProperty;
    }
    public void setUpdateableProperty(String updateableProperty) {
        this.updateableProperty = updateableProperty;
    }
    
}
