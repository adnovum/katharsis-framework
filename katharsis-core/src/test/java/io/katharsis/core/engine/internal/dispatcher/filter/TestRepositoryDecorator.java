package io.katharsis.core.engine.internal.dispatcher.filter;

import java.io.Serializable;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.repository.decorate.RepositoryDecoratorFactoryBase;
import io.katharsis.core.repository.decorate.ResourceRepositoryDecorator;
import io.katharsis.core.repository.decorate.ResourceRepositoryDecoratorBase;
import io.katharsis.core.mock.models.Schedule;
import io.katharsis.core.mock.repository.ScheduleRepository;

public class TestRepositoryDecorator extends RepositoryDecoratorFactoryBase {

	@SuppressWarnings("unchecked")
	@Override
	public <T, I extends Serializable> ResourceRepositoryDecorator<T, I> decorateRepository(
			ResourceRepositoryV2<T, I> repository) {
		if (repository.getResourceClass() == Schedule.class) {
			return (ResourceRepositoryDecorator<T, I>) new DecoratedScheduleRepository();
		}
		return null;
	}

	public static class DecoratedScheduleRepository extends ResourceRepositoryDecoratorBase<Schedule, Long>
			implements ScheduleRepository {

		@Override
		public ScheduleList findAll(QuerySpec querySpec) {
			return (ScheduleList) super.findAll(querySpec);
		}
	}
}
