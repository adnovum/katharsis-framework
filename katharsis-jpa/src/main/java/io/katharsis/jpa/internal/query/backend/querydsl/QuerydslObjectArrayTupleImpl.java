package io.katharsis.jpa.internal.query.backend.querydsl;

import java.util.Map;

import com.querydsl.core.types.Expression;
import io.katharsis.jpa.query.querydsl.QuerydslTuple;

public class QuerydslObjectArrayTupleImpl extends ObjectArrayTupleImpl implements QuerydslTuple {

	public QuerydslObjectArrayTupleImpl(Object entity, Map<String, Integer> selectionBindings) {
		super(entity, selectionBindings);
	}

	@Override
	public <T> T get(Expression<T> expr) {
		throw new UnsupportedOperationException();
	}
}
