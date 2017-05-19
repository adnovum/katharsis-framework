package io.katharsis.core.resource.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.katharsis.core.engine.properties.PropertiesProvider;
import io.katharsis.core.engine.internal.document.mapper.IncludeBehavior;
import io.katharsis.core.boot.KatharsisProperties;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.engine.document.Document;
import io.katharsis.core.engine.document.Relationship;
import io.katharsis.core.engine.document.Resource;
import io.katharsis.core.engine.document.ResourceIdentifier;
import io.katharsis.core.mock.models.HierarchicalTask;

@RunWith(MockitoJUnitRunner.class)
public class PerRootPathIncludeBehaviorTest extends AbstractIncludeBehaviorTest {

	@Override
	protected PropertiesProvider getPropertiesProvider() {
		return new PropertiesProvider() {

			@Override
			public String getProperty(String key) {
				if (key.equals(KatharsisProperties.INCLUDE_BEHAVIOR))
					return IncludeBehavior.PER_ROOT_PATH.toString();
				return null;
			}
		};
	}

	@Test
	public void includeParent() throws Exception {
		QuerySpec querySpec = new QuerySpec(HierarchicalTask.class);
		querySpec.includeRelation(Arrays.asList("parent"));

		Document document = mapper.toDocument(toResponse(h11), toAdapter(querySpec));
		Resource taskResource = document.getSingleData().get();

		Relationship parentRelationship = taskResource.getRelationships().get("parent");
		assertNotNull(parentRelationship);
		assertNotNull(parentRelationship.getSingleData());
		ResourceIdentifier parentResource = parentRelationship.getSingleData().get();
		assertNotNull(h1.getId().toString(), parentResource.getId());

		List<Resource> included = document.getIncluded();
		assertEquals(1, included.size());
		assertNotNull(h1.getId().toString(), included.get(0).getId());
		Relationship parentParentRelationship = included.get(0).getRelationships().get("parent");
		assertFalse(parentParentRelationship.getData().isPresent());
	}

	@Test
	public void includeParentParent() throws Exception {
		QuerySpec querySpec = new QuerySpec(HierarchicalTask.class);
		querySpec.includeRelation(Arrays.asList("parent", "parent"));

		Document document = mapper.toDocument(toResponse(h11), toAdapter(querySpec));
		Resource taskResource = document.getSingleData().get();

		Relationship parentRelationship = taskResource.getRelationships().get("parent");
		assertNotNull(parentRelationship);
		assertNotNull(parentRelationship.getSingleData());
		ResourceIdentifier parentResource = parentRelationship.getSingleData().get();
		assertNotNull(h1.getId().toString(), parentResource.getId());

		List<Resource> included = document.getIncluded();
		assertEquals(2, included.size());
		assertNotNull(h1.getId().toString(), included.get(0).getId());
		assertNotNull(h.getId().toString(), included.get(1).getId());
	}
}
