package io.katharsis.meta.internal;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.katharsis.core.engine.internal.repository.ResourceRepositoryAdapter;
import io.katharsis.core.engine.internal.information.resource.AnnotationResourceInformationBuilder;
import io.katharsis.core.engine.internal.utils.ClassUtils;
import io.katharsis.core.engine.internal.utils.PreconditionUtil;
import io.katharsis.legacy.registry.DefaultResourceInformationBuilderContext;
import io.katharsis.meta.information.MetaAwareInformation;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.MetaDataObject;
import io.katharsis.meta.model.MetaElement;
import io.katharsis.meta.model.MetaPrimaryKey;
import io.katharsis.meta.model.resource.MetaJsonObject;
import io.katharsis.meta.model.resource.MetaResource;
import io.katharsis.meta.model.resource.MetaResourceAction;
import io.katharsis.meta.model.resource.MetaResourceAction.MetaRepositoryActionType;
import io.katharsis.meta.model.resource.MetaResourceBase;
import io.katharsis.meta.model.resource.MetaResourceField;
import io.katharsis.meta.model.resource.MetaResourceRepository;
import io.katharsis.meta.provider.MetaProviderBase;
import io.katharsis.meta.provider.MetaProviderContext;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.engine.information.repository.RepositoryAction;
import io.katharsis.core.engine.information.repository.ResourceRepositoryInformation;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.engine.information.resource.ResourceField;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceFieldType;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.resource.links.LinksInformation;
import io.katharsis.core.resource.list.ResourceListBase;
import io.katharsis.core.resource.meta.MetaInformation;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.utils.Optional;
import io.katharsis.core.engine.parser.TypeParser;

public class ResourceMetaProviderImpl extends MetaProviderBase {

	private boolean useResourceRegistry;

	public ResourceMetaProviderImpl(boolean useResourceRegistry) {
		this.useResourceRegistry = useResourceRegistry;
	}

	@Override
	public Set<Class<? extends MetaElement>> getMetaTypes() {
		return new HashSet<>(
				Arrays.asList(MetaResource.class, MetaJsonObject.class, MetaResourceBase.class, MetaResourceField.class));
	}

	@Override
	public boolean accept(Type type, Class<? extends MetaElement> metaClass) {
		if (metaClass != MetaResource.class && metaClass != MetaElement.class && metaClass != MetaResourceBase.class
				&& metaClass != MetaJsonObject.class) {
			return false;
		}

		// note that the resourceRegistry might also contain none JSON API objects with a custom
		// information builder, so only accept if a MetaResource was explicitly requested
		if (this.useResourceRegistry) {
			ResourceRegistry resourceRegistry = context.getModuleContext().getResourceRegistry();
			if (resourceRegistry != null && (metaClass == MetaResource.class)) {
				Class<?> clazz = ClassUtils.getRawType(type);
				if (resourceRegistry.getEntryForClass(clazz) != null) {
					return true;
				}
			}
		}

		// always accept if json api annotations are found
		return metaClass == MetaResourceBase.class || ClassUtils.getRawType(type).getAnnotation(JsonApiResource.class) != null;
	}

	@Override
	public MetaElement createElement(Type type) {
		boolean allowNonResourceBaseClass = type != MetaResource.class;
		ResourceInformation information = getResourceInformation(ClassUtils.getRawType(type), allowNonResourceBaseClass);

		Class<?> resourceClass = information.getResourceClass();

		Class<?> superClass = resourceClass.getSuperclass();
		ResourceInformationBuilder resourceInformationBuilder = this.context.getModuleContext().getResourceInformationBuilder();

		MetaDataObject superMeta = null;
		if (superClass != Object.class) {
			// super type is either MetaResource or MetaResourceBase
			superMeta = context.getLookup().getMeta(superClass, MetaResourceBase.class);
		}

		String resourceType = information.getResourceType();
		MetaResourceBase resource = resourceType != null ? new MetaResource() : new MetaResourceBase();
		resource.setElementType(resource);
		resource.setImplementationType(resourceClass);
		resource.setSuperType(superMeta);
		if (superMeta != null) {
			((MetaDataObject) superMeta).addSubType(resource);
		}
		resource.setName(resourceClass.getSimpleName());

		if (resourceType != null) {
			((MetaResource) resource).setResourceType(resourceType);
		}

		List<ResourceField> fields = information.getFields();
		for (ResourceField field : fields) {
			addAttribute(resource, field);
		}

		return resource;
	}

	@Override
	public void discoverElements() {
		if (useResourceRegistry) {
			ResourceRegistry resourceRegistry = context.getModuleContext().getResourceRegistry();

			// enforce setup of meta data
			for (RegistryEntry entry : resourceRegistry.getResources()) {
				ResourceInformation information = entry.getResourceInformation();
				MetaResource metaResource = context.getLookup().getMeta(information.getResourceClass(), MetaResource.class);
				ResourceRepositoryInformation repositoryInformation = entry.getRepositoryInformation();
				ResourceRepositoryAdapter<?, Serializable> resourceRepository = entry.getResourceRepository(null);
				if (resourceRepository != null) {
					MetaResourceRepository repository = discoverRepository(repositoryInformation, metaResource,
							resourceRepository, context);
					context.add(repository);
				}
			}
		}
	}

	private MetaResourceRepository discoverRepository(ResourceRepositoryInformation repositoryInformation,
			MetaResource metaResource, ResourceRepositoryAdapter<?, Serializable> resourceRepository,
			MetaProviderContext context) {

		MetaResourceRepository meta = new MetaResourceRepository();
		meta.setResourceType(metaResource);
		meta.setName(metaResource.getName() + "Repository");
		meta.setId(metaResource.getId() + "Repository");

		for (RepositoryAction action : repositoryInformation.getActions().values()) {
			MetaResourceAction metaAction = new MetaResourceAction();
			metaAction.setName(action.getName());
			metaAction.setActionType(MetaRepositoryActionType.valueOf(action.getActionType().toString()));
			metaAction.setParent(meta, true);
		}

		// TODO avoid use of ResourceRepositoryAdapter by enriching ResourceRepositoryInformation
		Object repository = resourceRepository.getResourceRepository();
		if (repository instanceof ResourceRepositoryV2) {
			setListInformationTypes(repository, context, meta);
		}
		return meta;
	}

	private void setListInformationTypes(Object repository, MetaProviderContext context, MetaResourceRepository meta) {

		try {
			Method findMethod = repository.getClass().getMethod("findAll", QuerySpec.class);
			Class<?> listType = findMethod.getReturnType();

			if (ResourceListBase.class.equals(listType.getSuperclass())
					&& listType.getGenericSuperclass() instanceof ParameterizedType) {
				ParameterizedType genericSuperclass = (ParameterizedType) listType.getGenericSuperclass();

				Class<?> metaType = ClassUtils.getRawType(genericSuperclass.getActualTypeArguments()[1]);
				Class<?> linksType = ClassUtils.getRawType(genericSuperclass.getActualTypeArguments()[2]);
				if (!metaType.equals(MetaInformation.class)) {
					MetaDataObject listMetaType = context.getLookup().getMeta(metaType, MetaJsonObject.class);
					meta.setListMetaType(listMetaType);
				}
				if (!linksType.equals(LinksInformation.class)) {
					MetaDataObject listLinksType = context.getLookup().getMeta(linksType, MetaJsonObject.class);
					meta.setListLinksType(listLinksType);
				}
			}
		}
		catch (SecurityException | NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void onInitialized(MetaElement element) {
		if (element instanceof MetaResourceBase) {
			MetaResourceBase metaResource = (MetaResourceBase) element;

			ResourceInformation information = getResourceInformation(metaResource.getImplementationClass(), true);
			PreconditionUtil.assertNotNull(information.getResourceType(), metaResource);
			for (ResourceField field : information.getRelationshipFields()) {
				if (field.getOppositeName() != null) {
					Class<?> oppositeType = ClassUtils.getRawType(field.getElementType());
					MetaResource oppositeMeta = context.getLookup().getMeta(oppositeType, MetaResource.class);
					MetaAttribute attr = metaResource.getAttribute(field.getUnderlyingName());
					MetaAttribute oppositeAttr = oppositeMeta.getAttribute(field.getOppositeName());
					PreconditionUtil.assertNotNull(attr.getId() + " opposite not found", oppositeAttr);
					attr.setOppositeAttribute(oppositeAttr);
				}
			}

			ResourceField idField = information.getIdField();
			if (idField != null) {
				MetaAttribute idAttr = metaResource.getAttribute(idField.getUnderlyingName());
				idAttr.setPrimaryKeyAttribute(true);

				if (metaResource.getSuperType() == null || metaResource.getSuperType().getPrimaryKey() == null) {
					MetaPrimaryKey primaryKey = new MetaPrimaryKey();
					primaryKey.setName(metaResource.getName() + "$primaryKey");
					primaryKey.setName(metaResource.getId() + "$primaryKey");
					primaryKey.setElements(Arrays.asList(idAttr));
					primaryKey.setUnique(true);
					primaryKey.setParent(metaResource, true);
					metaResource.setPrimaryKey(primaryKey);
					context.add(primaryKey);
				}
			}

			// enrich with information from underlying meta model if available
			if (information instanceof MetaAwareInformation
					&& ((MetaAwareInformation<?>) information).getProjectedMetaElement().isPresent()) {
				MetaDataObject projectedMeta = ((MetaAwareInformation<MetaDataObject>) information).getProjectedMetaElement()
						.get();
				if (metaResource.getPrimaryKey() != null && projectedMeta.getPrimaryKey() != null) {
					metaResource.getPrimaryKey().setGenerated(projectedMeta.getPrimaryKey().isGenerated());
				}
			}

		}

		if (element instanceof MetaAttribute && element.getParent() instanceof MetaResourceBase) {
			MetaAttribute attr = (MetaAttribute) element;
			MetaResourceBase parent = (MetaResourceBase) attr.getParent();
			
			ResourceInformation information = getResourceInformation(parent.getImplementationClass(), true);
			ResourceField field = information.findFieldByName(attr.getName());
			if(field == null){
				throw new IllegalStateException("field not found for" + attr.getId());
			}
			
			Type implementationType = field.getGenericType();
			MetaElement metaType = context.getLookup().getMeta(implementationType, MetaJsonObject.class);
			attr.setType(metaType.asType());
		}

	}

	private ResourceInformation getResourceInformation(Class<?> resourceClass, boolean allowNonResourceBaseClass) {
		if (useResourceRegistry) {
			ResourceRegistry resourceRegistry = context.getModuleContext().getResourceRegistry();
			RegistryEntry entry = resourceRegistry.getEntryForClass(resourceClass);
			if (entry != null) {
				PreconditionUtil.assertNotNull(resourceClass.getName(), entry);
				return entry.getResourceInformation();
			}
		}

		ResourceInformationBuilder infoBuilder = context.getModuleContext().getResourceInformationBuilder();
		if (infoBuilder.accept(resourceClass)) {
			return infoBuilder.build(resourceClass);
		}

		if (allowNonResourceBaseClass) {
			AnnotationResourceInformationBuilder fallbackBuilder = new AnnotationResourceInformationBuilder(
					new ResourceFieldNameTransformer());
			fallbackBuilder.init(new DefaultResourceInformationBuilderContext(infoBuilder, new TypeParser()));
			return fallbackBuilder.build(resourceClass, true);
		}

		throw new IllegalStateException("failed to get information for " + resourceClass.getName());
	}

	private void addAttribute(MetaResourceBase resource, ResourceField field) {
		if (resource.getSuperType() != null && resource.getSuperType().hasAttribute(field.getUnderlyingName())) {
			return; // nothing to do
		}

		MetaResourceField attr = new MetaResourceField();

		attr.setParent(resource, true);
		attr.setName(field.getUnderlyingName());
		attr.setAssociation(field.getResourceFieldType() == ResourceFieldType.RELATIONSHIP);
		attr.setMeta(field.getResourceFieldType() == ResourceFieldType.META_INFORMATION);
		attr.setLinks(field.getResourceFieldType() == ResourceFieldType.LINKS_INFORMATION);
		attr.setDerived(false);
		attr.setLazy(field.isLazy());
		attr.setSortable(field.getAccess().isSortable());
		attr.setFilterable(field.getAccess().isFilterable());
		attr.setInsertable(field.getAccess().isPostable());
		attr.setUpdatable(field.getAccess().isPatchable());
		
		boolean isPrimitive = ClassUtils.isPrimitiveType(field.getType());
		boolean isId = field.getResourceFieldType() == ResourceFieldType.ID;
		attr.setNullable(!isPrimitive && !isId);

		PreconditionUtil.assertFalse(attr.getName(),
				!attr.isAssociation() && MetaElement.class.isAssignableFrom(field.getElementType()));

		// enrich with information not available in the katharsis information model
		if (field instanceof MetaAwareInformation) {
			MetaAwareInformation<MetaAttribute> metaField = (MetaAwareInformation<MetaAttribute>) field;
			Optional<MetaAttribute> projectedElement = metaField.getProjectedMetaElement();
			if (projectedElement.isPresent()) {
				MetaAttribute projectedAttr = projectedElement.get();
				attr.setLob(projectedAttr.isLob());
				attr.setVersion(projectedAttr.isVersion()); 
				attr.setNullable(projectedAttr.isNullable());
				attr.setCascaded(projectedAttr.isCascaded());
			}
		}
	}
}
