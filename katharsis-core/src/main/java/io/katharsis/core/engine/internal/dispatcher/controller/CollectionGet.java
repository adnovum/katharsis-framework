package io.katharsis.core.engine.internal.dispatcher.controller;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
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
import io.katharsis.core.engine.parser.TypeParser;

public class CollectionGet extends ResourceIncludeField {

	public CollectionGet(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, DocumentMapper documentMapper) {
        super(resourceRegistry, objectMapper, typeParser, documentMapper);
    }

    /**
     * Check if it is a GET request for a collection of resources.
     */
    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return jsonPath.isCollection()
                && jsonPath instanceof ResourcePath
                && HttpMethod.GET.name().equals(requestType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter, RepositoryMethodParameterProvider
        parameterProvider, Document requestBody) {
        String resourceName = jsonPath.getElementName();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        if (registryEntry == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        Document responseDocument;
        ResourceRepositoryAdapter resourceRepository = registryEntry.getResourceRepository(parameterProvider);
        JsonApiResponse entities;
		if (jsonPath.getIds() == null || jsonPath.getIds().getIds().isEmpty()) {
            entities = resourceRepository.findAll(queryAdapter);
        } else {
            Class<? extends Serializable> idType = (Class<? extends Serializable>)registryEntry
                .getResourceInformation().getIdField().getType();
            Iterable<? extends Serializable> parsedIds = typeParser.parse((Iterable<String>) jsonPath.getIds().getIds(),
                idType);
            entities = resourceRepository.findAll(parsedIds, queryAdapter);
        }
        responseDocument = documentMapper.toDocument(entities, queryAdapter, parameterProvider);
        
        return new Response(responseDocument, 200);
    }
}
