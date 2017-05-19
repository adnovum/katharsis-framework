package io.katharsis.core.engine.internal.utils;

import java.lang.reflect.Method;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.core.utils.Optional;

public class MethodCacheTest {

	@Test
	public void testMethod() {
		MethodCache cache = new MethodCache();

		Optional<Method> method = cache.find(Date.class, "parse", String.class);
		Assert.assertTrue(method.isPresent());
		Assert.assertEquals("parse", method.get().getName());

		// check for cache hit with assertSame
		Optional<Method> method2 = cache.find(Date.class, "parse", String.class);
		Assert.assertSame(method, method2);
	}

	@Test
	public void testNonExistingMethod() {
		MethodCache cache = new MethodCache();
		Optional<Method> method = cache.find(Date.class, "doesNotExist", String.class);
		Assert.assertFalse(method.isPresent());
	}

}
