package io.katharsis.core.engine.internal.dispatcher.controller;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathIds;
import io.katharsis.core.engine.internal.dispatcher.path.ResourcePath;
import io.katharsis.core.engine.internal.repository.ResourceRepositoryAdapter;
import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.core.exception.ResourceNotFoundException;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.utils.Nullable;
import io.katharsis.core.engine.parser.TypeParser;

public class ResourceGet extends ResourceIncludeField {

	public ResourceGet(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, DocumentMapper documentMapper) {
		super(resourceRegistry, objectMapper, typeParser, documentMapper);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Checks if requested document method is acceptable - is a GET request for
	 * a document.
	 */
	@Override
	public boolean isAcceptable(JsonPath jsonPath, String requestType) {
		return !jsonPath.isCollection() && jsonPath instanceof ResourcePath && HttpMethod.GET.name().equals(requestType);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Passes the request to controller method.
	 */
	@Override
	public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter, RepositoryMethodParameterProvider parameterProvider, Document requestBody) {
		String resourceName = jsonPath.getElementName();
		PathIds resourceIds = jsonPath.getIds();
		RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
		if (registryEntry == null) {
			throw new ResourceNotFoundException(resourceName);
		}
		String id = resourceIds.getIds().get(0);

		@SuppressWarnings("unchecked")
		Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry.getResourceInformation().getIdField().getType();
		Serializable castedId = typeParser.parse(id, idClass);
		ResourceRepositoryAdapter resourceRepository = registryEntry.getResourceRepository(parameterProvider);
		JsonApiResponse entities = resourceRepository.findOne(castedId, queryAdapter);
		
		Document responseDocument = documentMapper.toDocument(entities, queryAdapter);
		
		// return explicit { data : null } if values found
		if(!responseDocument.getData().isPresent()){
			responseDocument.setData(Nullable.nullValue());
		}

		return new Response(responseDocument, 200);
	}

}
