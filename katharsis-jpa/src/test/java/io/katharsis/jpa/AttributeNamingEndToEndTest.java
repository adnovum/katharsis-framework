package io.katharsis.jpa;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.jpa.model.NamingTestEntity;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.resource.list.ResourceList;

public class AttributeNamingEndToEndTest extends AbstractJpaJerseyTest {

	@Test
	public void testCanStoreBasicAttributeValues() throws InstantiationException, IllegalAccessException {
		ResourceRepositoryV2<NamingTestEntity, Serializable> repo = client.getRepositoryForType(NamingTestEntity.class);

		NamingTestEntity test = new NamingTestEntity();
		test.setId(1L);
		test.setSEcondUpperCaseValue(13L);
		repo.create(test);

		ResourceList<NamingTestEntity> list = repo.findAll(new QuerySpec(NamingTestEntity.class));
		Assert.assertEquals(1, list.size());
		NamingTestEntity saved = list.get(0);
		Assert.assertEquals(saved.getSEcondUpperCaseValue(), test.getSEcondUpperCaseValue());
	}
}
