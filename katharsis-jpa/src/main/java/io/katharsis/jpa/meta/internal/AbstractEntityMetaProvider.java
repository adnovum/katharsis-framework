package io.katharsis.jpa.meta.internal;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import io.katharsis.core.internal.utils.ClassUtils;
import io.katharsis.jpa.internal.JpaResourceInformationBuilder;
import io.katharsis.jpa.meta.MetaEntityAttribute;
import io.katharsis.jpa.meta.MetaJpaDataObject;
import io.katharsis.meta.internal.MetaUtils;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.MetaDataObject;
import io.katharsis.meta.model.MetaElement;
import io.katharsis.meta.model.MetaPrimaryKey;

public abstract class AbstractEntityMetaProvider<T extends MetaJpaDataObject> extends AbstractJpaDataObjectProvider<T> {

	@Override
	public MetaElement createElement(Type type) {
		Class<?> rawClazz = ClassUtils.getRawType(type);
		Class<?> superClazz = rawClazz.getSuperclass();
		MetaElement superMeta = null;
		if (superClazz.getAnnotation(Entity.class) != null || superClazz.getAnnotation(MappedSuperclass.class) != null) {
			superMeta = context.getLookup().getMeta(superClazz, MetaJpaDataObject.class);
		}
		T meta = newDataObject();
		meta.setElementType(meta);
		meta.setName(rawClazz.getSimpleName());
		meta.setImplementationType(type);
		meta.setSuperType((MetaDataObject) superMeta);
		if (superMeta != null) {
			((MetaDataObject) superMeta).addSubType(meta);
		}
		createAttributes(meta);

		setKey(meta);

		return meta;
	}

	private void setKey(T meta) {
		if (meta.getPrimaryKey() == null) {
			boolean generated = false;
			ArrayList<MetaAttribute> pkElements = new ArrayList<>();
			for (MetaAttribute attr : meta.getAttributes()) {
				if (attr.getAnnotation(Id.class) != null || attr.getAnnotation(EmbeddedId.class) != null) {
					pkElements.add(attr);
					attr.setPrimaryKeyAttribute(true);

					boolean attrGenerated = attr.getAnnotation(GeneratedValue.class) != null;
					if (pkElements.size() == 1) {
						generated = attrGenerated;
					} else if (generated != attrGenerated) {
						throw new IllegalStateException("cannot mix generated and not-generated primary key elements for " + meta.getId());
					}
				}
			}
			if (!pkElements.isEmpty()) {
				MetaPrimaryKey primaryKey = new MetaPrimaryKey();
				primaryKey.setName(meta.getName() + "$primaryKey");
				primaryKey.setElements(pkElements);
				primaryKey.setUnique(true);
				primaryKey.setParent(meta, true);
				primaryKey.setGenerated(generated);
				meta.setPrimaryKey(primaryKey);

				if (pkElements.size() == 1) {
					// single pk element cannot be nullable
					MetaAttribute pkElement = pkElements.get(0);
					pkElement.setNullable(false);
				}
			}
		}
	}

	protected abstract T newDataObject();

	@Override
	protected MetaAttribute createAttribute(T metaDataObject, String name) {
		MetaEntityAttribute attr = new MetaEntityAttribute();
		attr.setName(name);
		attr.setParent(metaDataObject, true);
		return attr;
	}

	@Override
	protected void initAttribute(MetaAttribute attr) {
		ManyToMany manyManyAnnotation = attr.getAnnotation(ManyToMany.class);
		ManyToOne manyOneAnnotation = attr.getAnnotation(ManyToOne.class);
		OneToMany oneManyAnnotation = attr.getAnnotation(OneToMany.class);
		OneToOne oneOneAnnotation = attr.getAnnotation(OneToOne.class);
		Version versionAnnotation = attr.getAnnotation(Version.class);
		Lob lobAnnotation = attr.getAnnotation(Lob.class);
		Column columnAnnotation = attr.getAnnotation(Column.class);

		boolean idAttr = attr.getAnnotation(Id.class) != null || attr.getAnnotation(EmbeddedId.class) != null;
		boolean attrGenerated = attr.getAnnotation(GeneratedValue.class) != null;

		attr.setVersion(versionAnnotation != null);
		attr.setAssociation(manyManyAnnotation != null || manyOneAnnotation != null || oneManyAnnotation != null || oneOneAnnotation != null);

		attr.setLazy(JpaResourceInformationBuilder.isJpaLazy(attr.getAnnotations()));
		attr.setLob(lobAnnotation != null);
		attr.setFilterable(lobAnnotation == null);
		attr.setSortable(lobAnnotation == null);

		if(attr.getReadMethod() == null){
			throw new IllegalStateException("no getter found for " + attr.getParent().getName() + "." + attr.getName());
		}
		Class<?> attributeType = attr.getReadMethod().getReturnType();
		boolean isPrimitiveType = ClassUtils.isPrimitiveType(attributeType);
		boolean columnNullable = columnAnnotation == null || columnAnnotation.nullable();
		attr.setNullable(!isPrimitiveType && columnNullable);

		boolean hasSetter = attr.getWriteMethod() != null;
		attr.setInsertable(hasSetter && (columnAnnotation == null || columnAnnotation.insertable()) && !attrGenerated && versionAnnotation == null);
		attr.setUpdatable(hasSetter && (columnAnnotation == null || columnAnnotation.updatable()) && !idAttr && versionAnnotation == null);

	}


	@Override
	public void onInitialized(MetaElement element) {
		super.onInitialized(element);
		if (element.getParent() instanceof MetaJpaDataObject && element instanceof MetaAttribute && ((MetaAttribute) element).getOppositeAttribute() == null) {
			MetaAttribute attr = (MetaAttribute) element;
			String mappedBy = getMappedBy(attr);
			if (mappedBy != null) {

				MetaDataObject oppositeType = attr.getType().getElementType().asDataObject();
				if (!mappedBy.contains(".")) {
					MetaAttribute oppositeAttr = oppositeType.getAttribute(mappedBy);
					attr.setOppositeAttribute(oppositeAttr);
				} else {
					// references within embeddables not yet supported
				}
			}
		}
	}

	private String getMappedBy(MetaAttribute attr) {
		ManyToMany manyManyAnnotation = attr.getAnnotation(ManyToMany.class);
		OneToMany oneManyAnnotation = attr.getAnnotation(OneToMany.class);
		OneToOne oneOneAnnotation = attr.getAnnotation(OneToOne.class);
		String mappedBy = null;
		if (manyManyAnnotation != null) {
			mappedBy = manyManyAnnotation.mappedBy();
		}
		if (oneManyAnnotation != null) {
			mappedBy = oneManyAnnotation.mappedBy();
		}
		if (oneOneAnnotation != null) {
			mappedBy = oneOneAnnotation.mappedBy();
		}

		if (mappedBy != null && mappedBy.length() == 0) {
			mappedBy = null;
		}
		return mappedBy;
	}

	private boolean hasJpaAnnotations(MetaAttribute attribute) {
		List<Class<? extends Annotation>> annotationClasses = Arrays.asList(Id.class, EmbeddedId.class, Column.class, ManyToMany.class, ManyToOne.class, OneToMany.class, OneToOne.class, Version.class,
				ElementCollection.class);
		for (Class<? extends Annotation> annotationClass : annotationClasses) {
			if (attribute.getAnnotation(annotationClass) != null) {
				return true;
			}
		}
		return false;
	}
}
