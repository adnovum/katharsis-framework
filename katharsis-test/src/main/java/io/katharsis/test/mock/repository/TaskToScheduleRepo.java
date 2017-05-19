package io.katharsis.test.mock.repository;

import io.katharsis.test.mock.models.Schedule;
import io.katharsis.test.mock.models.Task;
import io.katharsis.core.repository.RelationshipRepositoryBase;

public class TaskToScheduleRepo extends RelationshipRepositoryBase<Task, Long, Schedule, Long> {

	public TaskToScheduleRepo() {
		super(Task.class, Schedule.class);
	}

}
