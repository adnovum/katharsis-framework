package io.katharsis.core.repository;

import java.io.Serializable;

import io.katharsis.core.exception.ResourceNotFoundException;
import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.resource.list.ResourceList;

/**
 * Base document which is used to operate on the resources. Each document should have a corresponding document
 * implementation.
 *
 * @param <T> Type of an entity
 * @param <I> Type of Identifier of an entity
 * @param <L> List type
 */
public interface ResourceRepositoryV2<T, I extends Serializable> extends Repository {

	/**
	 * @return the class returned by this document
	 */
	Class<T> getResourceClass();

	/**
	* Search one document with a given ID. If a document cannot be found, a {@link ResourceNotFoundException}
	* exception should be thrown.
	*
	* @param id an identifier of the document
	* @param querySpec querySpec sent along with the request as parameters
	* @return an instance of the document
	*/
	T findOne(I id, QuerySpec querySpec);

	/**
	 * Search for all of the resources. An instance of {@link QueryParams} can be used if necessary. If no
	 * resources can be found, an empty {@link Iterable} or <i>null</i> must be returned.
	 *
	 * @param querySpec querySpec sent along with the request as parameters
	 * @return a list of found resources
	 */
	ResourceList<T> findAll(QuerySpec querySpec);

	/**
	 * Search for resources constrained by a list of identifiers. An instance of {@link QueryParams} can be used if
	 * necessary. If no resources can be found, an empty {@link Iterable} or <i>null</i> must be returned.
	 *
	 * @param ids an {@link Iterable} of passed document identifiers
	 * @param querySpec querySpec sent along with the request as parameters
	 * @return a list of found resources
	 */
	ResourceList<T> findAll(Iterable<I> ids, QuerySpec querySpec);

	/**
	 * Saves a document. A Returning document must include assigned identifier created for the instance of document.
	 *
	 * @param entity document to be saved
	 * @param <S> type of the document
	 * @return saved document. Must include set identifier.
	 */
	<S extends T> S save(S entity);
	
	/**
	 * Creates a document. A Returning document must include assigned identifier created for the instance of document.
	 *
	 * @param entity document to be saved
	 * @param <S> type of the document
	 * @return saved document. Must include set identifier.
	 */
	<S extends T> S create(S entity);

	/**
	 * Removes a document identified by id parameter.
	 *
	 * @param id identified of the document to be removed
	 */
	void delete(I id);

}
