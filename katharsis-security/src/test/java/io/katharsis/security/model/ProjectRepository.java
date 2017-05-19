package io.katharsis.security.model;

import java.util.HashMap;
import java.util.Map;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryBase;
import io.katharsis.core.resource.list.DefaultResourceList;
import io.katharsis.core.resource.list.ResourceList;
import io.katharsis.security.ResourcePermissionInformationImpl;


public class ProjectRepository extends ResourceRepositoryBase<Project, Long> {

	private static final Map<Long, Project> PROJECTS = new HashMap<>();

	public ProjectRepository() {
		super(Project.class);
	}

	@Override
	public <S extends Project> S save(S entity) {
		PROJECTS.put(entity.getId(), entity);
		return entity;
	}

	@Override
	public ResourceList<Project> findAll(QuerySpec querySpec) {
		DefaultResourceList<Project> list = querySpec.apply(PROJECTS.values());
		list.setMeta(new ResourcePermissionInformationImpl());
		return list;
	}

	@Override
	public void delete(Long id) {
		PROJECTS.remove(id);
	}

	public static void clear() {
		PROJECTS.clear();
	}
}
