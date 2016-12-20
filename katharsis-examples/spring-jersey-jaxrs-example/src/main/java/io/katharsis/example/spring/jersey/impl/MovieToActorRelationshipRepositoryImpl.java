package io.katharsis.example.spring.jersey.impl;

import java.util.Iterator;

import io.katharsis.example.spring.jersey.intf.MovieRepository;
import io.katharsis.example.spring.jersey.intf.MovieToActorRelationshipRepository;
import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.example.spring.jersey.model.ActorList;
import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.RelationshipRepositoryBase;
import io.katharsis.utils.PreconditionUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class MovieToActorRelationshipRepositoryImpl extends RelationshipRepositoryBase<Movie, String, Actor, String> implements
		MovieToActorRelationshipRepository {

	private MovieRepository movieRepository;

	// Required for Spring to instantiate the class
	public MovieToActorRelationshipRepositoryImpl() {
		super(Movie.class, Actor.class);
	}

	@Override
	public ActorList findManyTargets(String sourceId, String fieldName, QuerySpec querySpec) {
		Movie movie = movieRepository.findOne(sourceId, new QuerySpec(Movie.class));
		switch (fieldName){
			case "actors":
				return new ActorList(querySpec.apply(movie.getActors()));
			default:
				return new ActorList();
		}
	}

	@Override
	public Actor findOneTarget(String sourceId, String fieldName, QuerySpec querySpec) { // NOSONAR ok to override since not deprecated
		ActorList actors = findManyTargets(sourceId, fieldName, querySpec);
		Iterator<Actor> iterator = actors.iterator();
		if(!iterator.hasNext()){
			return null;
		}
		else {
			Actor a = iterator.next();
			PreconditionUtil.assertFalse("Actor not unique", iterator.hasNext());
			return a;
		}
	}

	@Autowired
	public void setMovieRepository(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	public MovieRepository getMovieRepository() {
		return movieRepository;
	}


}
