package io.katharsis.core.engine.information.resource;

/**
 * A builder which creates ResourceInformation instances of a specific class.
 */
public interface ResourceInformationBuilder {

	/**
	 * @param resourceClass
	 *            document class
	 * @return true if this builder can process the provided document class
	 */
	boolean accept(Class<?> resourceClass);

	/**
	 * @param resourceClass
	 *            document class
	 * @return ResourceInformation for the provided document class.
	 */
	ResourceInformation build(Class<?> resourceClass);

	public void init(ResourceInformationBuilderContext context);

	public String getResourceType(Class<?> clazz);

}
