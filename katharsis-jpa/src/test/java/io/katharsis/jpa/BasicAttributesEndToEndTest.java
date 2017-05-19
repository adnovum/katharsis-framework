package io.katharsis.jpa;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.jpa.model.BasicAttributesTestEntity;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.resource.list.ResourceList;

public class BasicAttributesEndToEndTest extends AbstractJpaJerseyTest {

	@Test
	public void testCanStoreBasicAttributeValues() throws InstantiationException, IllegalAccessException {
		ResourceRepositoryV2<BasicAttributesTestEntity, Serializable> repo = client.getRepositoryForType(BasicAttributesTestEntity.class);

		BasicAttributesTestEntity test = new BasicAttributesTestEntity();
		test.setId(1L);
		test.setLongValue(15);
		test.setNullableBooleanValue(false);
		test.setBooleanValue(true);
		test.setLongValue(13L);
		test.setNullableLongValue(14L);
		repo.create(test);

		ResourceList<BasicAttributesTestEntity> list = repo.findAll(new QuerySpec(BasicAttributesTestEntity.class));
		Assert.assertEquals(1, list.size());
		BasicAttributesTestEntity saved = list.get(0);
		Assert.assertEquals(saved.getLongValue(), test.getLongValue());
		Assert.assertEquals(saved.getNullableBooleanValue(), test.getNullableBooleanValue());
		Assert.assertEquals(saved.getBooleanValue(), test.getBooleanValue());
		Assert.assertEquals(saved.getNullableBooleanValue(), test.getNullableBooleanValue());
	}
}
