package io.katharsis.core.engine.internal.dispatcher;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.controller.BaseController;
import io.katharsis.core.engine.internal.dispatcher.controller.CollectionGet;
import io.katharsis.core.engine.internal.dispatcher.controller.RelationshipsResourceDelete;
import io.katharsis.core.engine.internal.dispatcher.controller.RelationshipsResourceGet;
import io.katharsis.core.engine.internal.dispatcher.controller.RelationshipsResourcePatch;
import io.katharsis.core.engine.internal.dispatcher.controller.RelationshipsResourcePost;
import io.katharsis.core.engine.internal.dispatcher.controller.ResourceDelete;
import io.katharsis.core.engine.internal.dispatcher.controller.ResourceGet;
import io.katharsis.core.engine.internal.dispatcher.controller.ResourcePatch;
import io.katharsis.core.engine.internal.dispatcher.controller.ResourcePost;
import io.katharsis.core.engine.properties.PropertiesProvider;
import io.katharsis.core.engine.internal.dispatcher.controller.FieldResourceGet;
import io.katharsis.core.engine.internal.dispatcher.controller.FieldResourcePost;
import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

/**
 * This lookup gets all predefined Katharsis controllers.
 */
public class DefaultControllerLookup implements ControllerLookup {

    private ResourceRegistry resourceRegistry;
    private TypeParser typeParser;
    private ObjectMapper objectMapper;
    private DocumentMapper documentMapper;
	private PropertiesProvider propertiesProvider;

    public DefaultControllerLookup(ResourceRegistry resourceRegistry, PropertiesProvider propertiesProvider, TypeParser typeParser, ObjectMapper objectMapper, DocumentMapper documentMapper) {
        this.resourceRegistry = resourceRegistry;
        this.propertiesProvider = propertiesProvider;
        this.typeParser = typeParser;
        this.objectMapper = objectMapper;
        this.documentMapper = documentMapper;
    }

    @Override
    public Set<BaseController> getControllers() {
        Set<BaseController> controllers = new HashSet<>();
        controllers.add(new RelationshipsResourceDelete(resourceRegistry, typeParser));
        controllers.add(new RelationshipsResourcePatch(resourceRegistry, typeParser));
        controllers.add(new RelationshipsResourcePost(resourceRegistry, typeParser));
        controllers.add(new ResourceDelete(resourceRegistry, typeParser));
        controllers.add(new CollectionGet(resourceRegistry, objectMapper, typeParser, documentMapper));
        controllers.add(new FieldResourceGet(resourceRegistry, objectMapper, typeParser, documentMapper));
        controllers.add(new RelationshipsResourceGet(resourceRegistry, objectMapper, typeParser, documentMapper));
        controllers.add(new ResourceGet(resourceRegistry, objectMapper, typeParser, documentMapper));
        controllers.add(new FieldResourcePost(resourceRegistry, propertiesProvider, typeParser, objectMapper, documentMapper));
        controllers.add(new ResourcePatch(resourceRegistry, propertiesProvider, typeParser, objectMapper, documentMapper));
        controllers.add(new ResourcePost(resourceRegistry, propertiesProvider, typeParser, objectMapper, documentMapper));

        return controllers;
    }

}
