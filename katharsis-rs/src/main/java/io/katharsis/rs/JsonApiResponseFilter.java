package io.katharsis.rs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.UriInfo;

import io.katharsis.core.internal.boot.KatharsisBoot;
import io.katharsis.core.internal.resource.DocumentMapper;
import io.katharsis.repository.response.JsonApiResponse;
import io.katharsis.resource.Document;
import io.katharsis.resource.annotations.JsonApiResource;
import io.katharsis.resource.list.ResourceListBase;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ServiceUrlProvider;
import io.katharsis.rs.resource.registry.UriInfoServiceUrlProvider;
import io.katharsis.rs.type.JsonApiMediaType;
import io.katharsis.utils.Nullable;

/**
 * Uses the Katharsis {@link DocumentMapper} to create a JSON API response for
 * custom JAX-RS actions.
 */
public class JsonApiResponseFilter implements ContainerResponseFilter {

	private KatharsisFeature feature;

    public JsonApiResponseFilter(KatharsisFeature feature) {
        this.feature = feature;
	}

	/**
	 * Creates JSON API responses for custom JAX-RS actions returning Katharsis resources.
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		Object response = responseContext.getEntity();
		if (response == null) {
			return;
		}
		
		// only modify responses which contain a single or a list of Katharsis resources
		if (isResourceResponse(response)) {
			KatharsisBoot boot = feature.getBoot();
			ResourceRegistry resourceRegistry = boot.getResourceRegistry();
			DocumentMapper documentMapper = boot.getDocumentMapper();
			
			ServiceUrlProvider serviceUrlProvider = resourceRegistry.getServiceUrlProvider();
			try {
				UriInfo uriInfo = requestContext.getUriInfo();
				if (serviceUrlProvider instanceof UriInfoServiceUrlProvider) {
					((UriInfoServiceUrlProvider) serviceUrlProvider).onRequestStarted(uriInfo);
				}

				JsonApiResponse jsonApiResponse = new JsonApiResponse();
				jsonApiResponse.setEntity(response);
				// use the Katharsis document mapper to create a JSON API response
				responseContext.setEntity(documentMapper.toDocument(jsonApiResponse, null));
				responseContext.getHeaders().put("Content-Type", Arrays.asList((Object)JsonApiMediaType.APPLICATION_JSON_API));
			}
			finally {
				if (serviceUrlProvider instanceof UriInfoServiceUrlProvider) {
					((UriInfoServiceUrlProvider) serviceUrlProvider).onRequestFinished();
				}
			}
		}
		else if (wrapResponse(responseContext)) {
			Document document = new Document();
			document.setData(Nullable.of(response));
			responseContext.setEntity(document);
		}
	}

	/**
	 * Determines whether the given response entity is either a Katharsis
	 * resource or a list of Katharsis resources.
	 * 
	 * @param response the response entity
	 * @return <code>true</code>, if <code>response</code> is a (list of)
	 *         Katharsis resource(s),<br />
	 *         <code>false</code>, otherwise
	 */
	private boolean isResourceResponse(Object response) {
		boolean singleResource = response.getClass().getAnnotation(JsonApiResource.class) != null;
		boolean resourceList = ResourceListBase.class.isAssignableFrom(response.getClass());
		return singleResource || resourceList;
	}

	/**
	 * Checks the media type of the response and the type of the response data to determine whether
	 * the response should be wrapped with the necessary JSON API tags.
	 * @param responseContext the context for the current response
	 * @return <code>true</code>, if the media type is {@link JsonApiMediaType#APPLICATION_JSON_API} and the
	 * 			response is not already in JSON API format,<br />
	 * 			<code>false</code>, otherwise.
	 */
	private boolean wrapResponse(ContainerResponseContext responseContext) {
		boolean mediaTypeJsonApi = JsonApiMediaType.APPLICATION_JSON_API_TYPE.equals(responseContext.getMediaType());
		Object response = responseContext.getEntity();
		boolean alreadyJsonApi = response instanceof Document || response instanceof InputStream;
		return mediaTypeJsonApi && !alreadyJsonApi;
	}

}