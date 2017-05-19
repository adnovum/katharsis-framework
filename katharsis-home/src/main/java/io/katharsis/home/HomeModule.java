package io.katharsis.home;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.katharsis.core.module.Module;
import io.katharsis.core.engine.http.HttpRequestContext;
import io.katharsis.core.engine.http.HttpRequestProcessor;
import io.katharsis.core.engine.information.repository.RepositoryInformation;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;

/**
 * Displays a list of available resources in the root directory.
 */
public class HomeModule implements Module {

	public static HomeModule newInstance() {
		return new HomeModule();
	}


	public static final String JSON_HOME_CONTENT_TYPE = "application/json-home";

	private HomeModule() {

	}

	@Override
	public String getModuleName() {
		return "home";
	}

	@Override
	public void setupModule(final ModuleContext context) {
		context.addHttpRequestProcessor(new HttpRequestProcessor() {
			@Override
			public void process(HttpRequestContext requestContext) throws IOException {
				if (isHomeRequest(requestContext)) {
					ObjectMapper objectMapper = context.getObjectMapper();
					ObjectNode node = objectMapper.createObjectNode();
					ObjectNode resourcesNode = node.putObject("resources");

					ResourceRegistry resourceRegistry = context.getResourceRegistry();
					for (RegistryEntry resourceEntry : resourceRegistry.getResources()) {
						RepositoryInformation repositoryInformation = resourceEntry.getRepositoryInformation();
						if (resourceEntry.getRepositoryInformation() != null) {
							String resourceType = repositoryInformation.getResourceInformation().getResourceType();
							String tag = "tag:" + resourceType;
							String href = "/" + resourceType + "/";
							ObjectNode resourceNode = resourcesNode.putObject(tag);
							resourceNode.put("href", href);
						}
					}

					String json = objectMapper.writeValueAsString(node);
					requestContext.setContentType(JSON_HOME_CONTENT_TYPE);
					requestContext.setResponse(200, json);
				}
			}
		});
	}


	public static boolean isHomeRequest(HttpRequestContext requestContext) {
		boolean isRoot = requestContext.getPath().isEmpty() || requestContext.getPath().equals("/");
		boolean acceptsJsonApi = requestContext.accepts(JSON_HOME_CONTENT_TYPE);
		boolean acceptsAny = requestContext
				.acceptsAny();
		boolean isGet = requestContext.getMethod().equalsIgnoreCase(HttpMethod.GET.toString());
		return isRoot && isGet && (acceptsJsonApi || acceptsAny);
	}


}
