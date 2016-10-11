package io.katharsis.jpa.query.querydsl;

import java.lang.reflect.Type;

public class ComputedAttributeRegistration {

	static TargetStep start(QuerydslQueryFactory querydslQueryFactory) {
		return new Builder(querydslQueryFactory);
	}

	private static class Builder implements TargetStep, AttributeNameStep, AttributeTypeStep, FactoryStep {
		private QuerydslQueryFactory queryFactory;
		private Class<?> targetClass;
		private String attributeName;
		private Type attributeType;
		private QuerydslExpressionFactory<?> expressionFactory;

		Builder(QuerydslQueryFactory queryFactory) {
			this.queryFactory = queryFactory;
		}

		@Override
		public AttributeNameStep onTarget(Class<?> targetClass) {
			this.targetClass = targetClass;
			return this;
		}

		@Override
		public AttributeTypeStep withName(String attributeName) {
			this.attributeName = attributeName;
			return this;
		}

		@Override
		public FactoryStep withType(Type attributeType) {
			this.attributeType = attributeType;
			return this;
		}

		@Override
		public void withFactory(QuerydslExpressionFactory<?> expressionFactory) {
			this.expressionFactory = expressionFactory;
			register();
		}

		private void register() {
			queryFactory.registerComputedAttribute(targetClass, attributeName, attributeType, expressionFactory);
		}
	}

	public interface TargetStep {
		AttributeNameStep onTarget(Class<?> targetClass);
	}

	public interface AttributeNameStep {
		AttributeTypeStep withName(String attributeName);
	}

	public interface AttributeTypeStep {
		FactoryStep withType(Type attributeType);
	}

	public interface FactoryStep {
		void withFactory(QuerydslExpressionFactory<?> expressionFactory);
	}

}
