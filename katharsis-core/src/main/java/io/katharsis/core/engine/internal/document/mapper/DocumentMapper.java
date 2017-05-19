package io.katharsis.core.engine.internal.document.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.properties.PropertiesProvider;
import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.legacy.internal.RepositoryMethodParameterProvider;
import io.katharsis.core.engine.query.QueryAdapter;
import io.katharsis.core.repository.response.JsonApiResponse;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.utils.Nullable;

public class DocumentMapper {

	private DocumentMapperUtil util;

	private ResourceMapper resourceMapper;

	private IncludeLookupSetter includeLookupSetter;

	private boolean client;

	protected PropertiesProvider propertiesProvider;

	public DocumentMapper(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, PropertiesProvider propertiesProvider) {
		this(resourceRegistry, objectMapper, propertiesProvider, false);
	}

	public DocumentMapper(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, PropertiesProvider propertiesProvider, boolean client) {
		this.propertiesProvider= propertiesProvider;
		this.util = new DocumentMapperUtil(resourceRegistry, objectMapper);
		this.resourceMapper = newResourceMapper(util, client, objectMapper);
		this.includeLookupSetter = new IncludeLookupSetter(resourceRegistry, resourceMapper, propertiesProvider);
		this.client = client;
	}

	protected ResourceMapper newResourceMapper(DocumentMapperUtil util, boolean client, ObjectMapper objectMapper) {
		return new ResourceMapper(util, client, objectMapper);
	}

	public Document toDocument(JsonApiResponse response, QueryAdapter queryAdapter) {
		return toDocument(response, queryAdapter, null);
	}

	public Document toDocument(JsonApiResponse response, QueryAdapter queryAdapter, RepositoryMethodParameterProvider parameterProvider) {
		Set<String> eagerLoadedRelations = Collections.emptySet();
		return toDocument(response, queryAdapter, parameterProvider, eagerLoadedRelations);
	}
	
	public Document toDocument(JsonApiResponse response, QueryAdapter queryAdapter, RepositoryMethodParameterProvider parameterProvider, Set<String> additionalEagerLoadedRelations) {
		if (response == null) {
			return null;
		}

		Document doc = new Document();
		addErrors(doc, response.getErrors());
		util.setMeta(doc, response.getMetaInformation());
		util.setLinks(doc, response.getLinksInformation());
		addData(doc, response.getEntity(), queryAdapter);
		addRelationDataAndInclusions(doc, response.getEntity(), queryAdapter, parameterProvider, additionalEagerLoadedRelations);

		return doc;
	}

	private void addRelationDataAndInclusions(Document doc, Object entity, QueryAdapter queryAdapter, RepositoryMethodParameterProvider parameterProvider, Set<String> additionalEagerLoadedRelations) {
		if (doc.getData().isPresent() && !client) {
			includeLookupSetter.setIncludedElements(doc, entity, queryAdapter, parameterProvider, additionalEagerLoadedRelations);
		}
	}

	private void addData(Document doc, Object entity, QueryAdapter queryAdapter) {
		if (entity != null) {
			if (entity instanceof Iterable) {
				ArrayList<Object> dataList = new ArrayList<>();
				for (Object obj : (Iterable<?>) entity) {
					dataList.add(resourceMapper.toData(obj, queryAdapter));
				}
				doc.setData(Nullable.of((Object) dataList));
			} else {
				doc.setData(Nullable.of((Object) resourceMapper.toData(entity, queryAdapter)));
			}
		}
	}

	private void addErrors(Document doc, Iterable<ErrorData> errors) {
		if (errors != null) {
			List<ErrorData> errorList = new ArrayList<>();
			for (ErrorData error : errors) {
				errorList.add(error);
			}
			doc.setErrors(errorList);
		}
	}

}
