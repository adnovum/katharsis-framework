package io.katharsis.meta.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.katharsis.core.internal.utils.ClassUtils;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.MetaDataObject;
import io.katharsis.meta.provider.MetaProviderBase;
import io.katharsis.resource.information.ResourceFieldNameTransformer;

public abstract class MetaDataObjectProviderBase<T extends MetaDataObject> extends MetaProviderBase {

	private static final ResourceFieldNameTransformer NAME_TRANSFORMER = new ResourceFieldNameTransformer();
	
	protected void createAttributes(T meta) {
		Class<?> implClass = meta.getImplementationClass();
		
		List<Field> fields = ClassUtils.getClassFields(implClass);
		List<Method> getters = ClassUtils.getClassGetters(implClass);
		
		Map<String, Field> fieldMap = toFieldMap(fields);
		Map<String, Method> getterMap = toMethodMap(getters);
		
		List<String> propertyNames = getOrderedPropertyNames(fields, getters, fieldMap);
		for (String name : propertyNames) {
			Method getterMethod = getterMap.get(name);
			if (getterMethod == null) {
				continue;
			}
			Method setterMethod = ClassUtils.findSetter(implClass, name, getterMethod.getReturnType());
			if (getterMethod.getDeclaringClass() != implClass) {
				continue; // contained in super type
			}
			MetaAttribute attribute = createAttribute(meta, MetaUtils.firstToLower(name));
			attribute.setReadMethod(getterMethod);
			attribute.setWriteMethod(setterMethod);
			initAttribute(attribute);
		}
	}
	
	protected void initAttribute(MetaAttribute attribute) {
	}

	private List<String> getOrderedPropertyNames(List<Field> fields, List<Method> getters, Map<String, Field> fieldMap) {
		List<String> propertyNames = new ArrayList<>();
		for(Field field : fields){
			propertyNames.add(field.getName());
		}
		for(Method method : getters){
			String name = NAME_TRANSFORMER.getMethodName(method);
			if(!fieldMap.containsKey(name)){
				propertyNames.add(name);
			}
		}
		return propertyNames;
	}

	private Map<String, Field> toFieldMap(List<Field> members) {
		Map<String, Field> map = new HashMap<>();
		for(Field member : members){
			map.put(member.getName(), member);
		}
		return map;
	}
	
	private Map<String, Method> toMethodMap(List<Method> members) {
		Map<String, Method> map = new HashMap<>();
		for(Method member : members){
			String name = NAME_TRANSFORMER.getMethodName(member);
			map.put(name, member);
		}
		return map;
	}

	protected MetaAttribute createAttribute(T metaDataObject, String name) {
		MetaAttribute attr = new MetaAttribute();
		attr.setName(MetaUtils.firstToLower(name));
		attr.setParent(metaDataObject, true);
		attr.setFilterable(true);
		attr.setSortable(true);
		return attr;
	}
}
