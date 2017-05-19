package io.katharsis.core.repository;

import io.katharsis.legacy.repository.RelationshipRepository;
import io.katharsis.legacy.repository.ResourceRepository;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.resource.links.LinksInformation;

/**
 * An optional interface that can be implemented along with {@link ResourceRepository} or {@link
 * RelationshipRepository} to get links information about returned document(s).
 * 
 * consisder the use ResourceList instead
 */
public interface LinksRepositoryV2<T> {

	/**
	 * Return meta information about a document. Can be called after find document methods call
	 *
	 * @param resources a list of found document(s)
	 * @param querySpec sent along with the request
	 * @return meta information object
	 */
	LinksInformation getLinksInformation(Iterable<T> resources, QuerySpec querySpec);
}