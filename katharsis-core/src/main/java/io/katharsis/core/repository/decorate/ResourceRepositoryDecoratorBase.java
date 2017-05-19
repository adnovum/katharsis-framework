package io.katharsis.core.repository.decorate;

import java.io.Serializable;

import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.resource.list.ResourceList;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.registry.ResourceRegistryAware;

public abstract class ResourceRepositoryDecoratorBase<T, I extends Serializable> implements ResourceRepositoryDecorator<T, I>, ResourceRegistryAware {

	protected ResourceRepositoryV2<T, I> decoratedObject;

	@Override
	public Class<T> getResourceClass() {
		return decoratedObject.getResourceClass();
	}

	@Override
	public T findOne(I id, QuerySpec querySpec) {
		return decoratedObject.findOne(id, querySpec);
	}

	@Override
	public ResourceList<T> findAll(QuerySpec querySpec) {
		return decoratedObject.findAll(querySpec);
	}

	@Override
	public ResourceList<T> findAll(Iterable<I> ids, QuerySpec querySpec) {
		return decoratedObject.findAll(ids, querySpec);
	}

	@Override
	public <S extends T> S save(S entity) {
		return decoratedObject.save(entity);
	}

	@Override
	public <S extends T> S create(S entity) {
		return decoratedObject.create(entity);
	}

	@Override
	public void delete(I id) {
		decoratedObject.delete(id);
	}

	@Override
	public void setDecoratedObject(ResourceRepositoryV2<T, I> decoratedObject) {
		this.decoratedObject = decoratedObject;
	}

	@Override
	public void setResourceRegistry(ResourceRegistry resourceRegistry) {
		if(decoratedObject instanceof ResourceRegistryAware){
			((ResourceRegistryAware) decoratedObject).setResourceRegistry(resourceRegistry);
		}
	}
}
