package io.katharsis.jpa.meta;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.jpa.model.AnnotationTestEntity;
import io.katharsis.jpa.model.TestEntity;
import io.katharsis.meta.MetaLookup;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.MetaKey;

public class JpaNullabilityMetaTest {

	@Test
	public void testPrimaryKeyNotNullable() {
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(TestEntity.class, MetaEntity.class);
		MetaKey primaryKey = meta.getPrimaryKey();
		MetaAttribute idField = primaryKey.getElements().get(0);
		Assert.assertFalse(idField.isNullable());
	}

	@Test
	public void testPrimitiveValueNotNullable() {
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(TestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("longValue");
		Assert.assertFalse(field.isNullable());
	}

	@Test
	public void testObjectValueNullable() {
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(TestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("stringValue");
		Assert.assertTrue(field.isNullable());
	}

	@Test
	public void testColumnAnnotatedValueIsNullable() {
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(AnnotationTestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("nullableValue");
		Assert.assertTrue(field.isNullable());
	}

	@Test
	public void testColumnAnnotatedValueIsNotNullable() {
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(AnnotationTestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("notNullableValue");
		Assert.assertFalse(field.isNullable());
	}
	
	@Test
	public void testNonOptionalRelatedValue(){
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(AnnotationTestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("nonOptionalRelatedValue");
		Assert.assertFalse(field.isNullable());
	}
	
	@Test
	public void testOptionalRelatedValue(){
		MetaLookup lookup = new MetaLookup();
		lookup.addProvider(new JpaMetaProvider());
		MetaEntity meta = lookup.getMeta(AnnotationTestEntity.class, MetaEntity.class);
		MetaAttribute field = meta.getAttribute("optionalRelatedValue");
		Assert.assertTrue(field.isNullable());
	}

}
