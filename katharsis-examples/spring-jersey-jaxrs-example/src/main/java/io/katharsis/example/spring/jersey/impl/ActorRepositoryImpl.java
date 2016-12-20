package io.katharsis.example.spring.jersey.impl;

import io.katharsis.example.spring.jersey.intf.ActorRepository;
import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.ResourceRepositoryBase;
import io.katharsis.resource.list.ResourceList;

public class ActorRepositoryImpl extends ResourceRepositoryBase<Actor, String> implements ActorRepository {

    public ActorRepositoryImpl() {
        super(Actor.class);
    }

    @Override
    public ResourceList<Actor> findAll(QuerySpec querySpec) {
        throw new UnsupportedOperationException("actor");
    }
}
