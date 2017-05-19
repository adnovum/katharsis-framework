package io.katharsis.jpa.meta.internal;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;

import io.katharsis.core.engine.internal.utils.ClassUtils;
import io.katharsis.jpa.meta.MetaEmbeddable;
import io.katharsis.jpa.meta.MetaEmbeddableAttribute;
import io.katharsis.jpa.meta.MetaJpaDataObject;
import io.katharsis.jpa.query.AnyTypeObject;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.MetaDataObject;
import io.katharsis.meta.model.MetaElement;

public class EmbeddableMetaProvider extends AbstractJpaDataObjectProvider<MetaEmbeddable> {

	private static final Object VALUE_ANYTYPE_ATTR_NAME = "value";
	
	@Override
	public Set<Class<? extends MetaElement>> getMetaTypes() {
		Set<Class<? extends MetaElement>> set = new HashSet<>();
		set.add(MetaEmbeddable.class);
		return set;
	}

	@Override
	public boolean accept(Type type, Class<? extends MetaElement> metaClass) {
		boolean hasAnnotation = ClassUtils.getRawType(type).getAnnotation(Embeddable.class) != null;
		boolean hasType = metaClass == MetaElement.class || metaClass == MetaEmbeddable.class || metaClass == MetaJpaDataObject.class;
		return hasAnnotation && hasType;
	}

	@Override
	public MetaEmbeddable createElement(Type type) {
		Class<?> rawClazz = ClassUtils.getRawType(type);
		Class<?> superClazz = rawClazz.getSuperclass();
		MetaElement superMeta = null;
		if (superClazz != Object.class) {
			superMeta = context.getLookup().getMeta(superClazz, MetaJpaDataObject.class);
		}
		MetaEmbeddable meta = new MetaEmbeddable();
		meta.setElementType(meta);
		meta.setName(rawClazz.getSimpleName());
		meta.setImplementationType(type);
		meta.setSuperType((MetaDataObject) superMeta);
		if (superMeta != null) {
			((MetaDataObject) superMeta).addSubType(meta);
		}
		createAttributes(meta);
		return meta;
	}

	@Override
	protected MetaAttribute createAttribute(MetaEmbeddable metaDataObject, String name) {
		MetaEmbeddableAttribute attr = new MetaEmbeddableAttribute();
		attr.setParent(metaDataObject, true);
		attr.setName(name);
		attr.setFilterable(true);
		attr.setSortable(true);
		
		if(AnyTypeObject.class.isAssignableFrom(metaDataObject.getImplementationClass()) && name.equals(VALUE_ANYTYPE_ATTR_NAME)){
			attr.setDerived(true);
		}
		
		return attr;
	}
}