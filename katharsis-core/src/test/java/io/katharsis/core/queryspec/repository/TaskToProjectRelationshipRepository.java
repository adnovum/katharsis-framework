package io.katharsis.core.queryspec.repository;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.RelationshipRepositoryBase;
import io.katharsis.core.repository.LinksRepositoryV2;
import io.katharsis.core.repository.MetaRepositoryV2;
import io.katharsis.core.resource.links.LinksInformation;
import io.katharsis.core.resource.meta.MetaInformation;
import io.katharsis.core.mock.models.Project;
import io.katharsis.core.mock.models.Task;

public class TaskToProjectRelationshipRepository extends RelationshipRepositoryBase<Task, Long, Project, Long>
		implements MetaRepositoryV2<Project>, LinksRepositoryV2<Project> {

	public TaskToProjectRelationshipRepository() {
		super(Task.class, Project.class);
	}

	@Override
	public LinksInformation getLinksInformation(Iterable<Project> resources, QuerySpec querySpec) {
		return new LinksInformation() {

			public String name = "value";
		};
	}

	@Override
	public MetaInformation getMetaInformation(Iterable<Project> resources, QuerySpec querySpec) {
		return new MetaInformation() {

			public String name = "value";
		};
	}

}