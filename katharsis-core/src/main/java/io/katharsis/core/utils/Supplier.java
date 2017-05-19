package io.katharsis.core.utils;

public interface Supplier<T> {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T get();
}
