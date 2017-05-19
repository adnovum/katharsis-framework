package io.katharsis.core.module;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.katharsis.core.module.discovery.ResourceLookup;

class TestResourceLookup implements ResourceLookup {

	@Override
	public Set<Class<?>> getResourceClasses() {
		return new HashSet<Class<?>>(Arrays.asList(TestResource.class));
	}

	@Override
	public Set<Class<?>> getResourceRepositoryClasses() {
		return new HashSet<Class<?>>(Arrays.asList(TestRepository.class));
	}
}