package io.katharsis.meta.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.katharsis.core.engine.internal.utils.ClassUtils;
import io.katharsis.core.engine.internal.utils.PreconditionUtil;
import io.katharsis.core.resource.annotations.JsonApiRelation;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.resource.annotations.SerializeType;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

@JsonApiResource(type = "meta/attribute")
public class MetaAttribute extends MetaElement {

	@JsonApiRelation(serialize = SerializeType.LAZY)
	private MetaType type;

	private boolean association;

	@JsonIgnore
	private Method readMethod;

	@JsonIgnore
	private Method writeMethod;

	@JsonIgnore
	private Field field;

	private boolean derived;

	private boolean lazy;

	private boolean version;

	private boolean primaryKeyAttribute;

	private boolean sortable;

	private boolean filterable;

	private boolean insertable;

	private boolean updatable;

	private boolean lob;

	private boolean nullable;

	private boolean cascaded;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	private MetaAttribute oppositeAttribute;

	private void initAccessors() {
		if (field == null || readMethod == null) {
			MetaDataObject parent = getParent();
			Class<?> beanClass = parent.getImplementationClass();
			String name = getName();

			this.field = ClassUtils.findClassField(beanClass, name);
			this.readMethod = ClassUtils.findGetter(beanClass, name);

			PreconditionUtil.assertFalse("no getter or field found ", field == null && readMethod == null);

			Class<?> rawType = field != null ? field.getType() : readMethod.getReturnType();
			writeMethod = ClassUtils.findSetter(beanClass, name, rawType);
		}
	}

	public boolean isCascaded() {
		return cascaded;
	}

	public void setCascaded(boolean cascade) {
		this.cascaded = cascade;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public Method getWriteMethod() {
		this.initAccessors();
		return writeMethod;
	}

	public Method getReadMethod() {
		this.initAccessors();
		if(readMethod == null){
			throw new IllegalStateException();
		}
		return readMethod;
	}

	@Override
	public MetaDataObject getParent() {
		return (MetaDataObject) super.getParent();
	}

	public boolean isAssociation() {
		return association;
	}

	public void setAssociation(boolean association) {
		this.association = association;
	}

	public MetaType getType() {
		return type;
	}

	public Object getValue(Object dataObject) {
		PropertyUtilsBean utils = BeanUtilsBean.getInstance().getPropertyUtils();
		try {
			return utils.getNestedProperty(dataObject, getName());
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("cannot access field " + getName() + " for " + dataObject.getClass().getName(), e);
		}
	}

	public void setValue(Object dataObject, Object value) {
		PropertyUtilsBean utils = BeanUtilsBean.getInstance().getPropertyUtils();
		try {
			utils.setNestedProperty(dataObject, getName(), value);
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("cannot access field " + getName() + " for " + dataObject.getClass().getName(), e);
		}
	}

	public MetaAttribute getOppositeAttribute() {
		return oppositeAttribute;
	}

	public boolean isDerived() {
		return derived;
	}

	public boolean isLazy() {
		return lazy;
	}

	public boolean isVersion() {
		return version;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addValue(Object dataObject, Object value) {
		Collection col = (Collection) getValue(dataObject);
		col.add(value);
	}

	@SuppressWarnings({ "rawtypes" })
	public void removeValue(Object dataObject, Object value) {
		Collection col = (Collection) getValue(dataObject);
		col.remove(value);
	}

	@JsonIgnore
	public <T extends Annotation> T getAnnotation(Class<T> clazz) {
		initAccessors();

		T annotation = null;
		if (field != null) {
			annotation = field.getAnnotation(clazz);
		}
		if (annotation == null && readMethod != null) {
			annotation = readMethod.getAnnotation(clazz);
		}
		if (annotation == null && writeMethod != null) {
			annotation = writeMethod.getAnnotation(clazz);
		}
		return annotation;
	}

	@JsonIgnore
	public Collection<Annotation> getAnnotations() {
		initAccessors();

		Collection<Annotation> annotations = new ArrayList<>();
		if (field != null) {
			annotations.addAll(Arrays.asList(field.getAnnotations()));
		}
		if (readMethod != null) {
			annotations.addAll(Arrays.asList(readMethod.getAnnotations()));
		}
		if (writeMethod != null) {
			annotations.addAll(Arrays.asList(writeMethod.getAnnotations()));
		}
		return annotations;
	}

	public void setDerived(boolean derived) {
		this.derived = derived;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public void setVersion(boolean version) {
		this.version = version;
	}

	public void setOppositeAttribute(MetaAttribute oppositeAttribue) {
		this.oppositeAttribute = oppositeAttribue;
	}

	public void setType(MetaType type) {
		this.type = type;
	}

	public boolean isPrimaryKeyAttribute() {
		return primaryKeyAttribute;
	}

	public void setPrimaryKeyAttribute(boolean primaryKeyAttribute) {
		this.primaryKeyAttribute = primaryKeyAttribute;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	/**
	 * @return true if it is a potentially large object
	 */
	public boolean isLob() {
		return lob;
	}

	public void setLob(boolean blob) {
		this.lob = blob;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}
}
