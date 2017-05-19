package io.katharsis.legacy.repository;

import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.core.resource.meta.MetaInformation;

/**
 * An optional interface that can be implemented along with
 * {@link ResourceRepository} or {@link RelationshipRepository} to get meta
 * information about returned document(s).
 * 
 * <b>Consider the use of ResourceList instead.</b>
 * 
 * @deprecated Make use of MetaRepositoryV2 or ResourceList
 */
@Deprecated
public interface MetaRepository<T> {

	/**
	 * Return meta information about a document. Can be called after find
	 * document methods call
	 *
	 * @param resources
	 *            a list of found document(s)
	 * @param queryParams
	 *            parameters sent along with the request
	 * @return meta information object
	 */
	MetaInformation getMetaInformation(Iterable<T> resources, QueryParams queryParams);
}
