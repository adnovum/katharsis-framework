package io.katharsis.core.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.core.module.discovery.ServiceDiscovery;
import io.katharsis.core.engine.dispatcher.RequestDispatcher;
import io.katharsis.core.engine.internal.exception.ExceptionMapperLookup;
import io.katharsis.core.engine.internal.exception.ExceptionMapperRegistry;
import io.katharsis.core.engine.error.ExceptionMapper;
import io.katharsis.core.engine.http.HttpRequestProcessor;
import io.katharsis.core.repository.decorate.RepositoryDecoratorFactory;
import io.katharsis.core.engine.filter.DocumentFilter;
import io.katharsis.core.engine.filter.RepositoryFilter;
import io.katharsis.core.engine.information.repository.RepositoryInformationBuilder;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.module.discovery.ResourceLookup;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.security.SecurityProvider;
import io.katharsis.core.engine.parser.TypeParser;

/**
 * Interface for extensions that can be registered to Katharsis to provide a
 * well-defined set of extensions on top of the default functionality.
 */
public interface Module {

	/**
	 * Returns the identifier of this module.
	 *
	 * @return module name
	 */
	String getModuleName();

	/**
	 * Called when the module is registered with Katharsis. Allows the module to
	 * register functionality it provides.
	 *
	 * @param context context
	 */
	void setupModule(ModuleContext context);

	/**
	 * Interface Katharsis exposes to modules for purpose of registering
	 * extended functionality.
	 */
	interface ModuleContext {

		void addHttpRequestProcessor(HttpRequestProcessor processor);

		ObjectMapper getObjectMapper();

		/**
		 * @return ServiceDiscovery
		 */
		ServiceDiscovery getServiceDiscovery();

		/**
		 * Register the given {@link ResourceInformationBuilder} in Katharsis.
		 *
		 * @param resourceInformationBuilder document information builder
		 */
		void addResourceInformationBuilder(ResourceInformationBuilder resourceInformationBuilder);

		/**
		 * Register the given {@link RepositoryInformationBuilder} in Katharsis.
		 *
		 * @param resourceInformationBuilder document information builder
		 */
		void addRepositoryInformationBuilder(RepositoryInformationBuilder repositoryInformationBuilder);

		/**
		 * Register the given {@link ResourceLookup} in Katharsis.
		 *
		 * @param resourceLookup document lookup
		 */
		void addResourceLookup(ResourceLookup resourceLookup);

		/**
		 * Registers an additional module for Jackson.
		 *
		 * @param module module
		 */
		void addJacksonModule(com.fasterxml.jackson.databind.Module module);

		/**
		 * Adds the given document for the given type.
		 */
		void addRepository(Object repository);

		/**
		 * Adds the given document for the given type.
		 *
		 * @param resourceClass document class
		 * @param repository document
		 * @deprecated
		 */
		void addRepository(Class<?> resourceClass, Object repository);

		/**
		 * Adds the given document for the given source and target type.
		 *
		 * @param sourceResourceClass source document class
		 * @param targetResourceClass target document class
		 * @param repository document
		 * @deprecated
		 */
		void addRepository(Class<?> sourceResourceClass, Class<?> targetResourceClass, Object repository);

		/**
		 * Adds a new exception mapper lookup.
		 *
		 * @param exceptionMapperLookup exception mapper lookup
		 */
		void addExceptionMapperLookup(ExceptionMapperLookup exceptionMapperLookup);

		/**
		 * Adds a new exception mapper lookup.
		 *
		 * @param exceptionMapper exception mapper
		 */
		void addExceptionMapper(ExceptionMapper<?> exceptionMapper);

		/**
		 * Adds a filter to intercept requests.
		 *
		 * @param filter filter
		 */
		void addFilter(DocumentFilter filter);

		/**
		 * Adds a document filter to intercept document calls.
		 *
		 * @param RepositoryFilter filter
		 */
		void addRepositoryFilter(RepositoryFilter filter);

		/**
		 * Adds a document decorator to intercept document calls.
		 *
		 * @param RepositoryDecoratorFactory decorator
		 */
		void addRepositoryDecoratorFactory(RepositoryDecoratorFactory decorator);

		/**
		 * Returns the ResourceRegistry. Note that instance is not yet available
		 * when {@link Module#setupModule(ModuleContext)} is called. So
		 * consumers may have to hold onto the {@link ModuleContext} instead.
		 *
		 * @return ResourceRegistry
		 */
		ResourceRegistry getResourceRegistry();

		/**
		 * Adds a securityProvider.
		 *
		 * @param securityProvider Ask remo
		 */
		void addSecurityProvider(SecurityProvider securityProvider);

		/**
		 * Returns the security provider. Provides access to security related
		 * feature independent of the underlying implementation.
		 */
		public SecurityProvider getSecurityProvider();

		/**
		 * @return if the module runs on the server-side
		 */
		public boolean isServer();

		public TypeParser getTypeParser();

		/**
		 * @return combined document information build registered by all modules
		 */
		public ResourceInformationBuilder getResourceInformationBuilder();

		public ExceptionMapperRegistry getExceptionMapperRegistry();

		RequestDispatcher getRequestDispatcher();

	}
}
