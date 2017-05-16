package io.katharsis.test.mock.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.katharsis.errorhandling.exception.ResourceNotFoundException;
import io.katharsis.legacy.repository.annotations.JsonApiDelete;
import io.katharsis.legacy.repository.annotations.JsonApiFindAll;
import io.katharsis.legacy.repository.annotations.JsonApiFindAllWithIds;
import io.katharsis.legacy.repository.annotations.JsonApiFindOne;
import io.katharsis.legacy.repository.annotations.JsonApiResourceRepository;
import io.katharsis.legacy.repository.annotations.JsonApiSave;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.test.mock.TestException;
import io.katharsis.test.mock.models.Task;

@JsonApiResourceRepository(Task.class)
public class TaskRepository {

	private static final ConcurrentHashMap<Long, Task> map = new ConcurrentHashMap<>();

	public static void clear() {
		map.clear();
	}

	@JsonApiSave
	public <S extends Task> S save(S entity) {

		if (entity.getId() == null) {
			entity.setId((long) (map.size() + 1));
		}
		map.put(entity.getId(), entity);

		if (entity.getId() == 10000) {
			throw new TestException("msg");
		}

		return entity;
	}

	@JsonApiFindOne
	public Task findOne(Long aLong, QuerySpec querySpec) {
		Task task = map.get(aLong);
		if (task == null) {
			throw new ResourceNotFoundException("failed to find resource with id " + aLong);
		}
		return task;
	}

	@JsonApiFindAll
	public Iterable<Task> findAll(QuerySpec queryParams) {
		return queryParams.apply(map.values());
	}

	@JsonApiFindAllWithIds
	public Iterable<Task> findAll(Iterable<Long> ids, QuerySpec queryParams) {
		List<Task> values = new LinkedList<>();
		for (Task value : map.values()) {
			if (contains(value, ids)) {
				values.add(value);
			}
		}
		return values;
	}

	private boolean contains(Task value, Iterable<Long> ids) {
		for (Long id : ids) {
			if (value.getId().equals(id)) {
				return true;
			}
		}

		return false;
	}

	@JsonApiDelete
	public void delete(Long aLong) {
		map.remove(aLong);
	}
}
