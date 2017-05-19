package io.katharsis.core.engine.internal.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;

import io.katharsis.core.utils.Optional;

public class MethodCache {

	private Map<MethodCacheKey, Optional<Method>> cache = new ConcurrentHashMap<>();

	public Optional<Method> find(Class<?> clazz, String name, Class<?>... parameters) {
		MethodCacheKey entry = new MethodCacheKey(clazz, name, parameters);
		Optional<Method> method = cache.get(entry);
		if (method == null) {
			try {
				method = Optional.of(clazz.getMethod(name, parameters));
			} catch (NoSuchMethodException e) { // NOSONAR
				method = Optional.empty();
			}
			cache.put(entry, method);
		}
		return method;
	}

	private static class MethodCacheKey {

		private Class<?> clazz;

		private String name;

		@SuppressWarnings("rawtypes")
		private Class[] parameters;

		public MethodCacheKey(Class<?> clazz, String name, Class<?>[] parameters) {
			this.clazz = clazz;
			this.name = name;
			this.parameters = parameters;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + Arrays.hashCode(parameters);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MethodCacheKey)) {
				return false;
			}
			MethodCacheKey other = (MethodCacheKey) obj;
			return ObjectUtils.equals(clazz, other.clazz) && ObjectUtils.equals(name, other.name) && Arrays.equals(parameters, other.parameters);
		}
	}
}
