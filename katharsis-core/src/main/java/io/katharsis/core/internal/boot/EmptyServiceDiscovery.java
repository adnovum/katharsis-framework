package io.katharsis.core.internal.boot;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.katharsis.module.ServiceDiscovery;

public class EmptyServiceDiscovery implements ServiceDiscovery {


	@Override
	public <T> List<T> getInstancesByType(Class<T> clazz) {
		return Collections.emptyList();
	}

	@Override
	public <A extends Annotation> List<Object> getInstancesByAnnotation(Class<A> annotation) {
		return Collections.emptyList();
	}

	private <T> List<T> getInstances(Set<Class<? extends T>> types) {
		return Collections.emptyList();
	}
}
