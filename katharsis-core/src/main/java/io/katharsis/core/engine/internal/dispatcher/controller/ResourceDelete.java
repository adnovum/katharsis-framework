package io.katharsis.core.engine.internal.dispatcher.controller;

import java.io.Serializable;

import io.katharsis.core.engine.internal.dispatcher.path.JsonPath;
import io.katharsis.core.engine.internal.dispatcher.path.PathIds;
import io.katharsis.core.engine.internal.dispatcher.path.ResourcePath;
import io.katharsis.core.exception.ResourceNotFoundException;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.engine.dispatcher.Response;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

public class ResourceDelete extends BaseController {

    private final ResourceRegistry resourceRegistry;
    private final TypeParser typeParser;

    public ResourceDelete(ResourceRegistry resourceRegistry, TypeParser typeParser) {
        this.resourceRegistry = resourceRegistry;
        this.typeParser = typeParser;
    }

    /**
     * {@inheritDoc}
     *
     * Checks if requested document method is acceptable - is a DELETE request for a document.
     */
    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection()
                && jsonPath instanceof ResourcePath
                && HttpMethod.DELETE.name().equals(requestType);
    }

    @Override
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter,
                                         RepositoryMethodParameterProvider parameterProvider, Document requestBody) {
        String resourceName = jsonPath.getElementName();
        PathIds resourceIds = jsonPath.getIds();
        RegistryEntry registryEntry = resourceRegistry.getEntry(resourceName);
        if (registryEntry == null) {
            //TODO: Add JsonPath toString and provide to exception?
            throw new ResourceNotFoundException(resourceName);
        }
        for (String id : resourceIds.getIds()) {
            @SuppressWarnings("unchecked") Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
                    .getResourceInformation()
                    .getIdField()
                    .getType();
            Serializable castedId = registryEntry.getResourceInformation().parseIdString(id);
            //noinspection unchecked
            registryEntry.getResourceRepository(parameterProvider).delete(castedId, queryAdapter);
        }

        return new Response(null, 204);
    }
}
