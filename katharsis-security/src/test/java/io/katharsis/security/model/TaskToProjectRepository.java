package io.katharsis.security.model;

import io.katharsis.core.repository.RelationshipRepositoryBase;

public class TaskToProjectRepository extends RelationshipRepositoryBase<Task, Long, Project, Long> {

	public TaskToProjectRepository() {
		super(Task.class, Project.class);
	}

}
