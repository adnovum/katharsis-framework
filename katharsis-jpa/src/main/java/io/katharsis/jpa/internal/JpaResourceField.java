package io.katharsis.jpa.internal;

import java.lang.reflect.Type;

import io.katharsis.core.engine.internal.information.resource.ResourceFieldImpl;
import io.katharsis.meta.information.MetaAwareInformation;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.core.resource.annotations.LookupIncludeBehavior;
import io.katharsis.core.engine.information.resource.ResourceFieldAccess;
import io.katharsis.core.engine.information.resource.ResourceFieldType;
import io.katharsis.core.utils.Optional;

public class JpaResourceField extends ResourceFieldImpl implements MetaAwareInformation<MetaAttribute> {

	private MetaAttribute projectedJpaAttribute;

	public JpaResourceField(MetaAttribute projectedJpaAttribute, String jsonName, String underlyingName,
			ResourceFieldType resourceFieldType, Class<?> type, Type genericType, String oppositeResourceType,
			String oppositeName, boolean lazy, boolean includeByDefault, LookupIncludeBehavior lookupIncludeBehavior,
			ResourceFieldAccess access) {
		super(jsonName, underlyingName, resourceFieldType, type, genericType, oppositeResourceType, oppositeName, lazy,
				includeByDefault, lookupIncludeBehavior, access);
		this.projectedJpaAttribute = projectedJpaAttribute;
	}

	@Override
	public Optional<MetaAttribute> getMetaElement() {
		return Optional.empty();
	}

	@Override
	public Optional<MetaAttribute> getProjectedMetaElement() {
		return Optional.of(projectedJpaAttribute);
	}

}
