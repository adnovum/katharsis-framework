package io.katharsis.meta.mock.model;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryBase;
import io.katharsis.core.resource.list.ResourceList;

public class TaskRepository extends ResourceRepositoryBase<Task, Long> {

	public TaskRepository() {
		super(Task.class);
	}

	@Override
	public ResourceList<Task> findAll(QuerySpec querySpec) {
		return null;
	}
}
