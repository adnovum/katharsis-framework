package io.katharsis.core.engine.internal.dispatcher.controller;

import java.io.Serializable;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.path.FieldPath;
import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathIds;
import io.katharsis.core.engine.properties.PropertiesProvider;
import io.katharsis.core.engine.internal.repository.RelationshipRepositoryAdapter;
import io.katharsis.core.engine.internal.repository.ResourceRepositoryAdapter;
import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.core.engine.internal.utils.Generics;
import io.katharsis.core.exception.RequestBodyException;
import io.katharsis.core.exception.RequestBodyNotFoundException;
import io.katharsis.core.exception.ResourceFieldNotFoundException;
import io.katharsis.core.exception.ResourceNotFoundException;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.http.HttpStatus;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.document.Resource;
import io.katharsis.core.engine.information.resource.ResourceField;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

/**
 * Creates a new post in a similar manner as in {@link ResourcePost}, but additionally adds a relation to a field.
 */
public class FieldResourcePost extends ResourceUpsert {
	
    public FieldResourcePost(ResourceRegistry resourceRegistry, PropertiesProvider propertiesProvider, TypeParser typeParser, @SuppressWarnings
        ("SameParameterValue") ObjectMapper objectMapper, DocumentMapper documentMapper) {
        super(resourceRegistry, propertiesProvider, typeParser, objectMapper, documentMapper);
    }

    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        if(jsonPath == null){
            throw new IllegalArgumentException();
        }
        return !jsonPath.isCollection()
            && FieldPath.class.equals(jsonPath.getClass())
            && HttpMethod.POST.name()
            .equals(requestType);
    }

    @Override
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter,
                                          RepositoryMethodParameterProvider parameterProvider, Document requestBody) {
        String resourceEndpointName = jsonPath.getResourceName();
        PathIds resourceIds = jsonPath.getIds();
        RegistryEntry endpointRegistryEntry = resourceRegistry.getEntry(resourceEndpointName);

        if (endpointRegistryEntry == null) {
            throw new ResourceNotFoundException(resourceEndpointName);
        }
        if (requestBody == null) {
            throw new RequestBodyNotFoundException(HttpMethod.POST, resourceEndpointName);
        }
        if (requestBody.isMultiple()) {
            throw new RequestBodyException(HttpMethod.POST, resourceEndpointName, "Multiple data in body");
        }

        Serializable castedResourceId = getResourceId(resourceIds, endpointRegistryEntry);
        ResourceField relationshipField = endpointRegistryEntry.getResourceInformation()
            .findRelationshipFieldByName(jsonPath.getElementName());
        if (relationshipField == null) {
            throw new ResourceFieldNotFoundException(jsonPath.getElementName());
        }

        Class<?> baseRelationshipFieldClass = relationshipField.getType();
        Class<?> relationshipFieldClass = Generics
            .getResourceClass(relationshipField.getGenericType(), baseRelationshipFieldClass);

        RegistryEntry relationshipRegistryEntry = resourceRegistry.findEntry(relationshipFieldClass);
        String relationshipResourceType = relationshipField.getOppositeResourceType();

        Resource dataBody = (Resource) requestBody.getData().get();
        Object resource = buildNewResource(relationshipRegistryEntry, dataBody, relationshipResourceType);
        setAttributes(dataBody, resource, relationshipRegistryEntry.getResourceInformation());
        ResourceRepositoryAdapter resourceRepository = relationshipRegistryEntry.getResourceRepository(parameterProvider);
        Document savedResourceResponse = documentMapper.toDocument(resourceRepository.create(resource, queryAdapter), queryAdapter, parameterProvider);
        saveRelations(queryAdapter, extractResource(savedResourceResponse), relationshipRegistryEntry, dataBody, parameterProvider);

        Serializable resourceId = relationshipRegistryEntry.getResourceInformation().parseIdString(savedResourceResponse.getSingleData().get().getId());

        RelationshipRepositoryAdapter relationshipRepositoryForClass = endpointRegistryEntry
            .getRelationshipRepositoryForClass(relationshipFieldClass, parameterProvider);

        @SuppressWarnings("unchecked")
        JsonApiResponse parent = endpointRegistryEntry.getResourceRepository(parameterProvider)
            .findOne(castedResourceId, queryAdapter);
        if (Iterable.class.isAssignableFrom(baseRelationshipFieldClass)) {
            //noinspection unchecked
            relationshipRepositoryForClass.addRelations(parent.getEntity(), Collections.singletonList(resourceId), relationshipField, queryAdapter);
        } else {
            //noinspection unchecked
            relationshipRepositoryForClass.setRelation(parent.getEntity(), resourceId, relationshipField, queryAdapter);
        }
        return new Response(savedResourceResponse, HttpStatus.CREATED_201);
    }

    private Serializable getResourceId(PathIds resourceIds, RegistryEntry registryEntry) {
        String resourceId = resourceIds.getIds()
            .get(0);
        @SuppressWarnings("unchecked")
        Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
            .getResourceInformation()
            .getIdField()
            .getType();
        return typeParser.parse(resourceId, idClass);
    }

	@Override
	protected boolean canModifyField(ResourceInformation resourceInformation, String fieldName, ResourceField field) {
		// allow dynamic field where field == null
		return field == null || field.getAccess().isPostable();
	}
}
