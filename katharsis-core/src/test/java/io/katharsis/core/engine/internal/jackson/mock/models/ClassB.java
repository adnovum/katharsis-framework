package io.katharsis.core.engine.internal.jackson.mock.models;

import java.util.Collections;
import java.util.List;

import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiIncludeByDefault;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.JsonApiToMany;
import io.katharsis.core.resource.annotations.JsonApiToOne;

@JsonApiResource(type = "classBs")
public class ClassB {

    @JsonApiId
    private Long id;

    @JsonApiToMany(lazy = false)
    private final List<ClassC> classCs;

    @JsonApiToOne
    private final ClassC classC;

    @JsonApiToOne
    @JsonApiIncludeByDefault
    private final ClassA classA;

    public ClassB() {
        this.classCs = null;
        this.classC = null;
        this.classA = null;
    }

    public ClassB(ClassC classCs, ClassC classC) {
        this.classCs = Collections.singletonList(classCs);
        this.classC = classC;
        this.classA = null;
    }

    public ClassB(ClassA classA) {
        this.classA = classA;
        this.classC = null;
        this.classCs = null;
    }

    public ClassB(ClassC classCs, ClassC classC, ClassA classA) {
        this.classCs = Collections.singletonList(classCs);
        this.classC = classC;
        this.classA = classA;
    }

    public Long getId() {
        return id;
    }

    public ClassB setId(Long id) {
        this.id = id;
        return this;
    }

    public List<ClassC> getClassCs() {
        return classCs;
    }

    public ClassC getClassC() {
        return classC;
    }

    public ClassA getClassA() {
        return classA;
    }
}
