package io.katharsis.core.mock.repository;

import io.katharsis.legacy.repository.annotations.JsonApiFindOne;
import io.katharsis.legacy.repository.annotations.JsonApiResourceRepository;
import io.katharsis.core.mock.models.Project;
import io.katharsis.core.mock.models.TaskWithLookup;

@JsonApiResourceRepository(TaskWithLookup.class)
public class TaskWithLookupRepository {

    @JsonApiFindOne
    public TaskWithLookup findOne(String id) {
        return new TaskWithLookup()
            .setId(id)
            .setProject(new Project().setId(42L))
            .setProjectOverridden(new Project().setId(42L));
    }
}
