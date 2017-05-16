package io.katharsis.rs;

import java.io.IOException;
import java.util.Arrays;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.katharsis.core.internal.boot.KatharsisBoot;
import io.katharsis.core.internal.dispatcher.HttpRequestContextBaseAdapter;
import io.katharsis.core.internal.resource.DocumentMapper;
import io.katharsis.module.http.HttpRequestContext;
import io.katharsis.module.http.HttpRequestContextProvider;
import io.katharsis.repository.response.JsonApiResponse;
import io.katharsis.resource.Document;
import io.katharsis.resource.annotations.JsonApiResource;
import io.katharsis.resource.list.ResourceListBase;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ServiceUrlProvider;
import io.katharsis.rs.type.JsonApiMediaType;
import io.katharsis.utils.Nullable;

/**
 * Uses the Katharsis {@link DocumentMapper} to create a JSON API response for
 * custom JAX-RS actions returning Katharsis resources.
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
			// TODO
			if (feature.getBoot().isNullDataResponseEnabled()) {
				Document document = new Document();
				document.setData(Nullable.nullValue());
				responseContext.setEntity(document);
				responseContext.setStatus(Response.Status.OK.getStatusCode());
				responseContext.getHeaders().put("Content-Type", Arrays.asList((Object) JsonApiMediaType.APPLICATION_JSON_API));
			}

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
				if (serviceUrlProvider instanceof HttpRequestContextProvider) {
					HttpRequestContext context = new HttpRequestContextBaseAdapter(new JaxrsRequestContext(requestContext,
							feature));
					((HttpRequestContextProvider) serviceUrlProvider).onRequestStarted(context);
				}

				JsonApiResponse jsonApiResponse = new JsonApiResponse();
				jsonApiResponse.setEntity(response);
				// use the Katharsis document mapper to create a JSON API response
				responseContext.setEntity(documentMapper.toDocument(jsonApiResponse, null));
				responseContext.getHeaders().put("Content-Type", Arrays.asList((Object) JsonApiMediaType.APPLICATION_JSON_API));

			}
			finally {
				if (serviceUrlProvider instanceof HttpRequestContextProvider) {
					((HttpRequestContextProvider) serviceUrlProvider).onRequestFinished();
				}
			}
		}
	}

	/**
	 * Determines whether the given response entity is either a Katharsis
	 * resource or a list of Katharsis resources.
	 *
	 * @param response the response entity
	 * @return <code>true</code>, if <code>response</code> is a (list of)
	 * Katharsis resource(s),<br />
	 * <code>false</code>, otherwise
	 */
	private boolean isResourceResponse(Object response) {
		boolean singleResource = response.getClass().getAnnotation(JsonApiResource.class) != null;
		boolean resourceList = ResourceListBase.class.isAssignableFrom(response.getClass());
		return singleResource || resourceList;
	}

	/**
	 * Checks the response's media type.
	 *
	 * @param responseContext the response context
	 * @return {@code true}, if media type is application/vnd.api+json,<br />
	 * {@code false}, otherwise
	 */
	private boolean isMediaTypeJsonApi(ContainerResponseContext responseContext) {
		return JsonApiMediaType.APPLICATION_JSON_API_TYPE.equals(responseContext.getMediaType());
	}

}