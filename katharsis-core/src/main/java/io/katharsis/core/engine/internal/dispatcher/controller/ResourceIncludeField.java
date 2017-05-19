package io.katharsis.core.engine.internal.dispatcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.document.mapper.DocumentMapper;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.parser.TypeParser;

/**
 * Created by zachncst on 10/14/15.
 */
public abstract class ResourceIncludeField extends BaseController {
    protected final ResourceRegistry resourceRegistry;
    protected final TypeParser typeParser;
    
	protected DocumentMapper documentMapper;

    public ResourceIncludeField(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, DocumentMapper documentMapper) {
        this.resourceRegistry = resourceRegistry;
        this.typeParser = typeParser;
        this.documentMapper = documentMapper;
    }
}
