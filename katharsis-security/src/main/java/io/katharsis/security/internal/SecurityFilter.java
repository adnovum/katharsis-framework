package io.katharsis.security.internal;

import io.katharsis.security.ResourcePermission;
import io.katharsis.security.ResourcePermissionInformation;
import io.katharsis.security.SecurityModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.katharsis.core.exception.ForbiddenException;
import io.katharsis.core.engine.filter.RepositoryFilterBase;
import io.katharsis.core.engine.filter.RepositoryFilterContext;
import io.katharsis.core.engine.filter.RepositoryMetaFilterChain;
import io.katharsis.core.engine.filter.RepositoryRequestFilterChain;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.dispatcher.RepositoryRequestSpec;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.resource.meta.MetaInformation;

public class SecurityFilter extends RepositoryFilterBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

	private SecurityModule module;

	public SecurityFilter(SecurityModule module) {
		this.module = module;
	}

	@Override
	public JsonApiResponse filterRequest(RepositoryFilterContext context, RepositoryRequestFilterChain chain) {
		RepositoryRequestSpec request = context.getRequest();
		QueryAdapter queryAdapter = request.getQueryAdapter();
		Class<?> resourceClass = queryAdapter.getResourceInformation().getResourceClass();

		HttpMethod method = request.getMethod();
		ResourcePermission requiredPermission = ResourcePermission.fromMethod(method);

		boolean allowed = module.isAllowed(resourceClass, requiredPermission);
		if (!allowed) {
			String msg = "user not allowed to access " + resourceClass.getName();
			throw new ForbiddenException(msg);
		}
		else {
			LOGGER.debug("user allowed to access {}", resourceClass.getSimpleName());
			return chain.doFilter(context);
		}
	}

	@Override
	public <T> MetaInformation filterMeta(RepositoryFilterContext context, Iterable<T> resources,
			RepositoryMetaFilterChain chain) {
		MetaInformation metaInformation = chain.doFilter(context, resources);
		if (metaInformation instanceof ResourcePermissionInformation) {
			ResourcePermissionInformation permissionInformation = (ResourcePermissionInformation) metaInformation;

			QueryAdapter queryAdapter = context.getRequest().getQueryAdapter();
			Class<?> resourceClass = queryAdapter.getResourceInformation().getResourceClass();
			permissionInformation.setResourcePermission(module.getResourcePermission(resourceClass));
		}
		return metaInformation;
	}

}
