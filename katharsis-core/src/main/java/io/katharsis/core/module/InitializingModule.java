package io.katharsis.core.module;

public interface InitializingModule extends Module {

	/**
	 * Called once Katharsis is fully initialized. From this point in time, the module is, for example,
	 * allowed to access the document registry.
	 */
	public void init();

}
