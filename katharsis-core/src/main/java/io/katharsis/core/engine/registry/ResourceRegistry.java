package io.katharsis.core.engine.registry;

import java.util.Collection;

import io.katharsis.core.engine.url.ServiceUrlProvider;
import io.katharsis.core.engine.information.resource.ResourceInformation;

public interface ResourceRegistry {

	public RegistryEntry addEntry(Class<?> clazz, RegistryEntry entry);

	public boolean hasEntry(Class<?> clazz);

	public RegistryEntry findEntry(Class<?> resourceClass);

	public RegistryEntry getEntry(String resourceType);

	public Collection<RegistryEntry> getResources();

	public RegistryEntry findEntry(String type, Class<?> clazz);

	public ServiceUrlProvider getServiceUrlProvider();

	public String getResourceUrl(ResourceInformation resourceInformation);

	public RegistryEntry getEntryForClass(Class<?> resourceClass);
	
	/**
	 * @param resourceType
	 * @return ResourceInformation of the the top most super type of the provided document.
	 */
	public ResourceInformation getBaseResourceInformation(String resourceType);

}
