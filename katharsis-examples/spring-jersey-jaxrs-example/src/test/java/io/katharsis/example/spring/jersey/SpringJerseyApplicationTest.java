package io.katharsis.example.spring.jersey;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import io.katharsis.client.KatharsisClient;
import io.katharsis.client.action.JerseyActionStubFactory;
import io.katharsis.example.spring.jersey.intf.MovieRepository;
import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.resource.list.ResourceList;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class SpringJerseyApplicationTest extends JerseyTest {

	private MovieRepository movieRepository;

	@Override
	protected Application configure() {
		return new ResourceConfig(SpringKatharsisFeature.class);
	}

	@Test
	public void test() throws Exception {
		KatharsisClient client = setupClient();
		movieRepository = client.getResourceRepository(MovieRepository.class);

		testGetMovies();
		testGetMovie();
		testGetMovieIncludeActors();
		testVote();
		testVoteAll();
	}

	private KatharsisClient setupClient() {
		KatharsisClient client = new KatharsisClient(getBaseUri().toString());
		client.setActionStubFactory(JerseyActionStubFactory.newInstance());
		client.getHttpAdapter().setReceiveTimeout(10000000, TimeUnit.MILLISECONDS);
		return client;
	}

	private void testGetMovies() {
		ResourceList<Movie> movies = fetchMovies();
		assertThat(movies, hasSize(2));
		final Movie bond = movies.get(0);
		assertThat(bond.getId(), is("bond"));
		assertThat(bond.getTitle(), is("Agent 007"));
		assertThat(bond.getYear(), is(1960));
		assertThat(bond.getActors(), hasSize(2));

		final Movie crash = movies.get(1);
		assertThat(crash.getId(), is("crash"));
		assertThat(crash.getTitle(), is("The Crash"));
		assertThat(crash.getYear(), is(2016));
		assertThat(crash.getActors(), hasSize(1));
	}

	private void testGetMovie() {
		final String movieId = "crash";
		final Movie movie = fetchMovie(movieId);

		assertThat(movie.getId(), is(movieId));
		assertThat(movie.getTitle(), is("The Crash"));
		assertThat(movie.getYear(), is(2016));
		assertThat(movie.getActors(), hasSize(1));
	}

	/**
	 * This test is redundant, since Katharsis automatically loads
	 * related collections when they are accessed.
	 */
	private void testGetMovieIncludeActors() {
		final String movieId = "bond";
		final Movie movie = fetchMovie(movieId, "actors");

		assertThat(movie.getId(), is(movieId));
		assertThat(movie.getTitle(), is("Agent 007"));
		assertThat(movie.getYear(), is(1960));
		assertThat(movie.getActors(), hasSize(2));

		List<Actor> actors = movie.getActors();
		Actor jane = actors.get(0);
		assertThat(jane.getId(), is("jane"));
		assertThat(jane.getName(), is("Jane Franklin"));
		// cannot access a circular reference to the collection of a related resource
		//assertThat(jane.getMovies(), hasSize(1));

		Actor may = actors.get(1);
		assertThat(may.getId(), is("may"));
		assertThat(may.getName(), is("James May"));
		// cannot access a circular reference to the collection of a related resource
		//assertThat(may.getMovies(), hasSize(1));
	}

	private void testVote() {
		final String movieId = "bond";
		Movie movie = fetchMovie(movieId);
		assertThat(movie.getMeta().getAverageStars(), is(4));

		String response = movieRepository.vote(movieId, 2);
		assertThat(response, is("updated"));

		movie = fetchMovie(movieId);
		assertThat(movie.getMeta().getAverageStars(), is(2));
	}

	private void testVoteAll() {
		String response = movieRepository.voteall(0);
		assertThat(response, is("all updated"));

		ResourceList<Movie> movies = fetchMovies();
		for (Movie movie : movies) {
			assertThat(movie.getMeta().getAverageStars(), is(0));
		}
	}

	private Movie fetchMovie(String movieId, String... relations) {
		QuerySpec querySpec = new QuerySpec(Movie.class);
		if (relations != null && relations.length > 0) {
			querySpec.includeRelation(new ArrayList<>(Arrays.asList(relations)));
		}
		return movieRepository.findOne(movieId, querySpec);
	}

	private ResourceList<Movie> fetchMovies(String... relations) {
		QuerySpec querySpec = new QuerySpec(Movie.class);
		if (relations != null && relations.length > 0) {
			querySpec.includeRelation(new ArrayList<>(Arrays.asList(relations)));
		}
		return movieRepository.findAll(querySpec);
	}

	private void assertResponseStatus(Response response, Response.Status status) {
		assertThat(response, is(notNullValue()));
		assertThat(Response.Status.fromStatusCode(response.getStatus()), is(status));
	}
}
