package io.katharsis.legacy.queryParams;

import java.math.BigDecimal;
import java.util.*;

import io.katharsis.core.internal.utils.StringUtils;
import io.katharsis.legacy.queryParams.include.Inclusion;
import io.katharsis.legacy.queryParams.params.*;
import io.katharsis.queryspec.*;
import io.katharsis.resource.information.ResourceInformation;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;

@SuppressWarnings({"deprecation"})
public class DefaultQueryParamsConverter implements QueryParamsConverter {

    private ResourceRegistry resourceRegistry;

    public DefaultQueryParamsConverter(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    @Override
    public QueryParams fromParams(Class<?> rootType, QuerySpec querySpec) {
        QueryParams params = new QueryParams();
        applyIncludedFields(querySpec, params);
        applySorting(querySpec, params);
        applyRelatedFields(querySpec, params);
        applyPaging(querySpec, params);
        applyFiltering(querySpec, params);

        params.setGrouping(new TypedParams<>(Collections.unmodifiableMap(new HashMap<String, GroupingParams>())));
        return params;
    }

    private String getResourceType(Class<?> resourceClass) {
        RegistryEntry registryEntry = resourceRegistry.getEntryForClass(resourceClass);
        if (registryEntry == null) {
            throw new IllegalArgumentException("resourceType for class " + resourceClass.getName() + " not found");
        }
        ResourceInformation resourceInformation = registryEntry.getResourceInformation();
        return resourceInformation.getResourceType();
    }

    protected void applyFiltering(QuerySpec spec, QueryParams queryParams) {
        List<FilterSpec> filters = spec.getFilters();
        Map<String, FilterParams> decodedFiltersMap = new LinkedHashMap<>();
        if (filters != null && !filters.isEmpty()) {
            String resourceType = getResourceType(spec.getResourceClass());
            Map<String, Set<String>> map = new LinkedHashMap<>();
            for (FilterSpec filter : filters) {
                String key = joinPath(filter.getAttributePath());
                if (filter.getOperator() != null && filter.getOperator() != FilterOperator.EQ)
                    key += "." + filter.getOperator().name();

                Set<String> valueSet = new LinkedHashSet<>();
                if (filter.getValue() instanceof Set) {
                    for (Object value : (Set<?>) filter.getValue()) {
                        valueSet.add(value.toString());
                    }
                } else {
                    valueSet.add(filter.getValue().toString());
                }
                map.put(key, valueSet);
            }
            decodedFiltersMap.put(resourceType, new FilterParams(map));
        }
        queryParams.setFilters(new TypedParams<>(Collections.unmodifiableMap(decodedFiltersMap)));
    }

    private String joinPath(List<String> pathList) {
        return StringUtils.join(".", pathList);
    }

    protected void applyIncludedFields(QuerySpec spec, QueryParams queryParams) {
        List<IncludeFieldSpec> includedFields = spec.getIncludedFields();
        Map<String, IncludedFieldsParams> decodedSparseMap = new LinkedHashMap<>();
        if (includedFields != null && !includedFields.isEmpty()) {
            String resourceType = getResourceType(spec.getResourceClass());
            Set<String> pathSet = new LinkedHashSet<>();
            for (IncludeFieldSpec includedField : includedFields) {
                String path = joinPath(includedField.getAttributePath());
                pathSet.add(path);
            }
            IncludedFieldsParams includedFieldsParams = new IncludedFieldsParams(pathSet);
            decodedSparseMap.put(resourceType, includedFieldsParams);
        }
        queryParams.setIncludedFields(new TypedParams<>(Collections.unmodifiableMap(decodedSparseMap)));
    }

    protected void applyRelatedFields(QuerySpec spec, QueryParams queryParams) {
        List<IncludeRelationSpec> includedRelations = spec.getIncludedRelations();
        Map<String, IncludedRelationsParams> decodedSparseMap = new LinkedHashMap<>();
        if (includedRelations != null && !includedRelations.isEmpty()) {
            String resourceType = getResourceType(spec.getResourceClass());
            Set<Inclusion> inclusions = new LinkedHashSet<>();
            for (IncludeRelationSpec relationSpec : includedRelations) {
                for (String attrPath : relationSpec.getAttributePath()) {
                    Inclusion inclusion = new Inclusion(attrPath);
                    inclusions.add(inclusion);
                }
            }
            IncludedRelationsParams includedRelationsParams = new IncludedRelationsParams(Collections.unmodifiableSet(inclusions));
            decodedSparseMap.put(resourceType, includedRelationsParams);
        }
        queryParams.setIncludedRelations(new TypedParams<>(Collections.unmodifiableMap(decodedSparseMap)));
    }

    protected void applySorting(QuerySpec spec, QueryParams queryParams) {
        List<SortSpec> sortSpecs = spec.getSort();
        Map<String, SortingParams> decodedSortingMap = new LinkedHashMap<>();
        if (sortSpecs != null && !sortSpecs.isEmpty()) {
            String resourceType = getResourceType(spec.getResourceClass());
            for (SortSpec sortSpec : sortSpecs) {
                Map<String, RestrictedSortingValues> sortingValues = new HashMap<>();
                String joinedPath = joinPath(sortSpec.getAttributePath());
                RestrictedSortingValues sortValue = sortSpec.getDirection() == Direction.DESC ? RestrictedSortingValues
                        .desc : RestrictedSortingValues.asc;
                SortingParams sortingParams = new SortingParams(sortingValues);
                sortingValues.put(joinedPath, sortValue);
                decodedSortingMap.put(resourceType, sortingParams);
            }
        }
        queryParams.setSorting(new TypedParams<>(Collections.unmodifiableMap(decodedSortingMap)));
    }

    protected void applyPaging(QuerySpec spec, QueryParams queryParams) {
        Long limit = spec.getLimit();
        long offset = spec.getOffset();
        Map<RestrictedPaginationKeys, Integer> decodedPagination = new LinkedHashMap<>();

        decodedPagination.put(RestrictedPaginationKeys.offset, new BigDecimal(offset).intValueExact());
        if (limit != null)
            decodedPagination.put(RestrictedPaginationKeys.limit, new BigDecimal(limit).intValueExact());

        queryParams.setPagination(Collections.unmodifiableMap(decodedPagination));
    }
}