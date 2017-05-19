package io.katharsis.meta.mock.model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.resource.links.DefaultPagedLinksInformation;
import io.katharsis.core.resource.links.LinksInformation;
import io.katharsis.core.resource.list.ResourceListBase;
import io.katharsis.core.resource.meta.MetaInformation;

@Path("schedules")
public interface ScheduleRepository extends ResourceRepositoryV2<Schedule, Long> {

	@GET
	@Path("repositoryAction")
	public String repositoryAction(@QueryParam(value = "msg") String msg);

	@GET
	@Path("{id}/resourceAction")
	public String resourceAction(@PathParam("id") long id, @QueryParam(value = "msg") String msg);

	@Override
	public ScheduleList findAll(QuerySpec querySpec);

	class ScheduleList extends ResourceListBase<Schedule, ScheduleListMeta, ScheduleListLinks> {

	}

	class ScheduleListLinks extends DefaultPagedLinksInformation implements LinksInformation {

		public String name = "value";
	}

	class ScheduleListMeta implements MetaInformation {

		public String name = "value";

	}
}
