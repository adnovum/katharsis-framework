package io.katharsis.core.mock.models;

import io.katharsis.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "memoranda")
public class Memorandum extends Document {
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
