package io.katharsis.jpa;

import io.katharsis.jpa.mapping.JpaMapper;

public class RegisterMapping {

	private RegisterMapping() {
		throw new UnsupportedOperationException();
	}

	static EntityStep start(JpaModule jpaModule) {
		return new Builder(jpaModule);
	}

	@SuppressWarnings("rawtypes")
	private static class Builder implements EntityStep {

		private JpaModule jpaModule;
		private Class entityClass;
		private Class dtoClass;
		private JpaMapper mapper;

		private Builder(JpaModule jpaModule) {
			this.jpaModule = jpaModule;
		}

		@Override
		public <S> DtoStep<S> entityClass(Class<S> entityClass) {
			this.entityClass = entityClass;
			return new DtoStep<S>() {
				@Override
				public <T> MapperStep<S, T> dtoClass(Class<T> dtoClass) {
					return Builder.this.dtoClass(dtoClass);
				}
			};
		}

		private <S, T> MapperStep<S, T> dtoClass(Class<T> dtoClass) {
			this.dtoClass = dtoClass;
			return new MapperStep<S, T>() {
				@Override
				public void mapper(JpaMapper<S, T> mapper) {
					Builder.this.mapper(mapper);
				}

			};
		}

		private <S, T> void mapper(JpaMapper<S, T> mapper) {
			this.mapper = mapper;
			register();
		}

		@SuppressWarnings("unchecked")
		private void register() {
			jpaModule.addMappedEntityClass(entityClass, dtoClass, mapper);
		}
	}

	public interface EntityStep {
		<S> DtoStep<S> entityClass(Class<S> entityClass);
	}

	public interface DtoStep<S> {
		<T> MapperStep<S, T> dtoClass(Class<T> dtoClass);
	}

	public interface MapperStep<S, T> {
		void mapper(JpaMapper<S, T> mapper);
	}

}
