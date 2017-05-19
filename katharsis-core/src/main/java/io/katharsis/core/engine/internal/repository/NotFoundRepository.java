package io.katharsis.core.engine.internal.repository;

import java.io.Serializable;

import io.katharsis.core.exception.RepositoryNotFoundException;
import io.katharsis.legacy.queryParams.QueryParams;
import io.katharsis.legacy.repository.ResourceRepository;

/**
 * Represents a non-existing document. It is assigned to a document class if Katharsis couldn't find any document.
 */
public class NotFoundRepository<T, ID extends Serializable> implements ResourceRepository<T, ID> {

    private final Class<?> repositoryClass;

    public NotFoundRepository(Class<? extends T> repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    @Override
    public T findOne(ID id, QueryParams queryParams) {
        throw new RepositoryNotFoundException(repositoryClass);
    }

    @Override
    public Iterable<T> findAll(QueryParams queryParams) {
        throw new RepositoryNotFoundException(repositoryClass);
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids, QueryParams queryParams) {
        throw new RepositoryNotFoundException(repositoryClass);
    }

    @Override
    public void delete(ID id) {
        throw new RepositoryNotFoundException(repositoryClass);
    }

    @Override
    public <S extends T> S save(S entity) {
        throw new RepositoryNotFoundException(repositoryClass);
    }
}
