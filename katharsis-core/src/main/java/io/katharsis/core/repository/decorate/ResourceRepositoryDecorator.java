package io.katharsis.core.repository.decorate;

import java.io.Serializable;

import io.katharsis.core.engine.internal.utils.Decorator;
import io.katharsis.core.repository.ResourceRepositoryV2;

public interface ResourceRepositoryDecorator<T, I extends Serializable> extends ResourceRepositoryV2<T, I>, Decorator<ResourceRepositoryV2<T, I>> {

	@Override
	public void setDecoratedObject(ResourceRepositoryV2<T, I> decoratedObject);
}
