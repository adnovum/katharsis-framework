package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "things")
public abstract class Thing {

    @JsonApiId
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
