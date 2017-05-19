package io.katharsis.core.engine.registry;

/**
 * Can be used by repositories to obtain a ResourceRegistry instance.
 */
public interface ResourceRegistryAware {

	public void setResourceRegistry(ResourceRegistry resourceRegistry);
}
