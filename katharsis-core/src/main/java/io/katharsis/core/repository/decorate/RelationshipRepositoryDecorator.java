package io.katharsis.core.repository.decorate;

import java.io.Serializable;

import io.katharsis.core.engine.internal.utils.Decorator;
import io.katharsis.core.repository.RelationshipRepositoryV2;

public interface RelationshipRepositoryDecorator<T, I extends Serializable, D, J extends Serializable>
		extends RelationshipRepositoryV2<T, I, D, J>, Decorator<RelationshipRepositoryV2<T, I, D, J>> {

	@Override
	public void setDecoratedObject(RelationshipRepositoryV2<T, I, D, J> decoratedObject);
}
