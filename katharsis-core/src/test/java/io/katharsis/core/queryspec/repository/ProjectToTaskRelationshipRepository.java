package io.katharsis.core.queryspec.repository;

import io.katharsis.core.repository.RelationshipRepositoryBase;
import io.katharsis.core.mock.models.Project;
import io.katharsis.core.mock.models.Task;

public class ProjectToTaskRelationshipRepository extends RelationshipRepositoryBase<Project, Long, Task, Long> {

	public ProjectToTaskRelationshipRepository() {
		super(Project.class, Task.class);
	}

}