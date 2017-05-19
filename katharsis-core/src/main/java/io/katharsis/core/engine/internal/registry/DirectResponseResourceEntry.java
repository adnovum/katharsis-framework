package io.katharsis.core.engine.internal.registry;

import io.katharsis.legacy.registry.RepositoryInstanceBuilder;
import io.katharsis.legacy.repository.ResourceRepository;
import io.katharsis.core.engine.registry.ResourceEntry;

public class DirectResponseResourceEntry implements ResourceEntry {
    private final RepositoryInstanceBuilder<ResourceRepository> repositoryInstanceBuilder;

    public DirectResponseResourceEntry(RepositoryInstanceBuilder<ResourceRepository> repositoryInstanceBuilder) {
        this.repositoryInstanceBuilder = repositoryInstanceBuilder;
    }

    public Object getResourceRepository() {
        return repositoryInstanceBuilder.buildRepository();
    }

    @Override
    public String toString() {
        return "DirectResponseResourceEntry{" +
            "repositoryInstanceBuilder=" + repositoryInstanceBuilder +
            '}';
    }
}
