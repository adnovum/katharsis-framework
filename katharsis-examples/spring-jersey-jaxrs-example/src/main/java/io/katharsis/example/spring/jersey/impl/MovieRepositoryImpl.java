package io.katharsis.example.spring.jersey.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.katharsis.example.spring.jersey.intf.MovieRepository;
import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.example.spring.jersey.model.MovieList;
import io.katharsis.example.spring.jersey.model.MovieMeta;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.ResourceRepositoryBase;

public class MovieRepositoryImpl extends ResourceRepositoryBase<Movie, String> implements MovieRepository {

	private Map<String,Movie> movies = new HashMap<>();

	public MovieRepositoryImpl() {
		super(Movie.class);
		Movie bond = new Movie();
		Movie crash = new Movie();
		bond.setId("bond");
		bond.setTitle("Agent 007");
		bond.setYear(1960);
		ArrayList<Actor> actors = new ArrayList<>();
		Actor may = new Actor();
		may.setId("may");
		may.setName("James May");
		may.setMovies(Collections.singletonList(bond));
		Actor jane = new Actor();
		jane.setId("jane");
		jane.setName("Jane Franklin");
		jane.setMovies(Arrays.asList(bond,crash));
		actors.add(jane);
		actors.add(may);
		bond.setActors(actors);
		bond.setMeta(new MovieMeta(4));

		crash.setId("crash");
		crash.setYear(2016);
		crash.setTitle("The Crash");
		crash.setActors(Collections.singletonList(jane));
		crash.setMeta(new MovieMeta(5));

		movies.put(bond.getId(), bond);
		movies.put(crash.getId(), crash);
	}

	@Override
	public MovieList findAll(QuerySpec querySpec) {
		return new MovieList(querySpec.apply(movies.values()));
	}

	@Override
	public Movie findOne(String id, QuerySpec querySpec) {
		return super.findOne(id, querySpec);
	}

	@Override
	public String vote(String id, int stars) {
		Movie m = movies.get(id);
		if(m != null) {
			m.getMeta().setAverageStars(stars);
			return "updated";
		}
		return "ignored";
	}
}
