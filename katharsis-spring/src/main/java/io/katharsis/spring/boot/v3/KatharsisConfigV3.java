package io.katharsis.spring.boot.v3;

import javax.servlet.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.katharsis.core.internal.boot.KatharsisBoot;
import io.katharsis.core.internal.boot.PropertiesProvider;
import io.katharsis.core.internal.jackson.JsonApiModuleBuilder;
import io.katharsis.core.properties.KatharsisProperties;
import io.katharsis.module.ModuleRegistry;
import io.katharsis.resource.registry.ConstantServiceUrlProvider;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.spring.SpringKatharsisFilter;
import io.katharsis.spring.boot.KatharsisSpringBootProperties;
import io.katharsis.spring.internal.SpringServiceDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Current katharsis configuration with JSON API compliance, QuerySpec and module support.
 * Note that there is no support for QueryParams is this version due to the lack of JSON API compatibility.
 */
@Configuration
@EnableConfigurationProperties(KatharsisSpringBootProperties.class)
public class KatharsisConfigV3 implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	private KatharsisSpringBootProperties properties;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public SpringServiceDiscovery discovery() {
		return new SpringServiceDiscovery();
	}

	@Bean
	public KatharsisBoot katharsisBoot(SpringServiceDiscovery serviceDiscovery) {
		KatharsisBoot boot = new KatharsisBoot();
		boot.setObjectMapper(objectMapper);

		if (properties.getDomainName() != null && properties.getPathPrefix() != null) {
			String baseUrl = properties.getDomainName() + properties.getPathPrefix();
			boot.setServiceUrlProvider(new ConstantServiceUrlProvider(baseUrl));
		}
		boot.setServiceDiscovery(serviceDiscovery);
		boot.setDefaultPageLimit(properties.getDefaultPageLimit());
		boot.setMaxPageLimit(properties.getMaxPageLimit());
		boot.setPropertiesProvider(new PropertiesProvider() {
			@Override
			public String getProperty(String key) {
				if (KatharsisProperties.RESOURCE_SEARCH_PACKAGE.equals(key)) {
					return properties.getResourcePackage();
				}
				if (KatharsisProperties.RESOURCE_DEFAULT_DOMAIN.equals(key)) {
					return properties.getDomainName();
				}
				if (KatharsisProperties.WEB_PATH_PREFIX.equals(key)) {
					return properties.getPathPrefix();
				}
				return applicationContext.getEnvironment().getProperty(key);
			}
		});
		boot.boot();
		return boot;
	}

	@Bean
	public Filter springBootSampleKatharsisFilter(KatharsisBoot boot) {
		JsonApiModuleBuilder jsonApiModuleBuilder = new JsonApiModuleBuilder();
		SimpleModule parameterNamesModule = jsonApiModuleBuilder.build(boot.getResourceRegistry(), false);

		objectMapper.registerModule(parameterNamesModule);

		return new SpringKatharsisFilter(boot);
	}

	@Bean
	public ResourceRegistry resourceRegistry(KatharsisBoot boot) {
		return boot.getResourceRegistry();
	}

	@Bean
	public ModuleRegistry moduleRegistry(KatharsisBoot boot) {
		return boot.getModuleRegistry();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
