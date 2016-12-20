package io.katharsis.example.spring.jersey.intf;

import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.example.spring.jersey.model.ActorList;
import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.RelationshipRepositoryV2;

public interface MovieToActorRelationshipRepository extends RelationshipRepositoryV2<Movie, String, Actor, String> {

	@Override
	ActorList findManyTargets(String sourceId, String fieldName, QuerySpec querySpec);

}
