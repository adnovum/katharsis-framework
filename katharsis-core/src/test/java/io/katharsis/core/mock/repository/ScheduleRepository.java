package io.katharsis.core.mock.repository;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.resource.links.DefaultPagedLinksInformation;
import io.katharsis.core.resource.links.LinksInformation;
import io.katharsis.core.resource.list.ResourceListBase;
import io.katharsis.core.resource.meta.MetaInformation;
import io.katharsis.core.mock.models.Schedule;

public interface ScheduleRepository extends ResourceRepositoryV2<Schedule, Long> {

	class ScheduleList extends ResourceListBase<Schedule, ScheduleListMeta, ScheduleListLinks> {

	}

	class ScheduleListLinks extends DefaultPagedLinksInformation implements LinksInformation {

		public String name = "value";
	}

	class ScheduleListMeta implements MetaInformation {

		public String name = "value";

	}

	@Override
	public ScheduleList findAll(QuerySpec querySpec);
}
