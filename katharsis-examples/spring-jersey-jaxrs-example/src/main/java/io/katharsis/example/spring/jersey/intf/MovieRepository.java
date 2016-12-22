package io.katharsis.example.spring.jersey.intf;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.example.spring.jersey.model.MovieList;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.ResourceRepositoryV2;

@Path("movie")
public interface MovieRepository extends ResourceRepositoryV2<Movie, String> {

	@Override
	MovieList findAll(QuerySpec querySpec);


	@POST
	@Path("{id}/vote")
	public String vote(@PathParam("id") String id, @QueryParam(value = "stars") int stars);

	@POST
	@Path("voteall")
	String voteall(@QueryParam(value = "stars") int stars);

}
