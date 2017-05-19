package io.katharsis.core.engine.internal.dispatcher.controller;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.path.FieldPath;
import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathIds;
import io.katharsis.core.engine.internal.repository.RelationshipRepositoryAdapter;
import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.core.engine.internal.utils.Generics;
import io.katharsis.core.exception.ResourceFieldNotFoundException;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.information.resource.ResourceField;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

public class FieldResourceGet extends ResourceIncludeField {

    public FieldResourceGet(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, DocumentMapper documentMapper) {
        super(resourceRegistry, objectMapper, typeParser, documentMapper);
    }

    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection()
                && FieldPath.class.equals(jsonPath.getClass())
                && HttpMethod.GET.name().equals(requestType);
    }

    @Override
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter, RepositoryMethodParameterProvider
            parameterProvider, Document requestBody) {
        String resourceName = jsonPath.getResourceName();
        PathIds resourceIds = jsonPath.getIds();

        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        Serializable castedResourceId = getResourceId(resourceIds, registryEntry);
        String elementName = jsonPath.getElementName();
        ResourceField relationshipField = registryEntry.getResourceInformation().findRelationshipFieldByName(elementName);
        if (relationshipField == null) {
            throw new ResourceFieldNotFoundException(elementName);
        }

        Class<?> baseRelationshipFieldClass = relationshipField.getType();
       
        Class<?> relationshipFieldClass = Generics.getResourceClass(relationshipField.getGenericType(), baseRelationshipFieldClass);

        RelationshipRepositoryAdapter relationshipRepositoryForClass = registryEntry
                .getRelationshipRepositoryForClass(relationshipFieldClass, parameterProvider);
        JsonApiResponse entities; 
		if (Iterable.class.isAssignableFrom(baseRelationshipFieldClass)) {
        	entities = relationshipRepositoryForClass.findManyTargets(castedResourceId, relationshipField, queryAdapter);
        } else {
        	entities = relationshipRepositoryForClass.findOneTarget(castedResourceId, relationshipField, queryAdapter);
        }
        Document responseDocument = documentMapper.toDocument(entities, queryAdapter, parameterProvider);

        return new Response(responseDocument, 200);
    }

    private Serializable getResourceId(PathIds resourceIds, RegistryEntry registryEntry) {
        String resourceId = resourceIds.getIds().get(0);
        @SuppressWarnings("unchecked")
        Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
                .getResourceInformation()
                .getIdField()
                .getType();
        return typeParser.parse(resourceId, idClass);
    }
}
