package io.katharsis.example.spring.jersey;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.example.spring.jersey.model.Movie;
import io.katharsis.rs.type.JsonApiMediaType;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class SpringJerseyApplicationTest extends JerseyTest {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected Application configure() {
		return new ResourceConfig(SpringKatharsisFeature.class);
	}

	@Test
	public void test() throws Exception {
		testGetMovies();
		testGetMovie();
		testGetMovieIncludeActors();
		testVote();
	}

	public void testGetMovies() throws Exception {
		Response response = target("/movie").request().get();
		assertResponseStatus(response, Response.Status.OK);
		assertHeader(response.getHeaders(), HttpHeaders.CONTENT_TYPE, JsonApiMediaType.APPLICATION_JSON_API);

		JsonNode data = mapper.readTree((InputStream) response.getEntity()).get("data");
		assertThat(data.getNodeType(), is(JsonNodeType.ARRAY));
		List<Movie> movies = new ArrayList<>();
		for (JsonNode node : data) {
			movies.add(getMovieFromJson(node));
		}
		assertThat(movies, hasSize(2));
		final Movie bond = movies.get(0);
		assertThat(bond.getId(), is("bond"));
		assertThat(bond.getTitle(), is("Agent 007"));
		assertThat(bond.getYear(), is(1960));
		assertThat(bond.getActors(), is(nullValue()));

		final Movie crash = movies.get(1);
		assertThat(crash.getId(), is("crash"));
		assertThat(crash.getTitle(), is("The Crash"));
		assertThat(crash.getYear(), is(2016));
		assertThat(crash.getActors(), is(nullValue()));
	}

	public void testGetMovie() throws Exception {
		final String movieId = "crash";
		final Movie movie = fetchMovie(movieId);

		assertThat(movie.getId(), is(movieId));
		assertThat(movie.getTitle(), is("The Crash"));
		assertThat(movie.getYear(), is(2016));
		assertThat(movie.getActors(), is(nullValue()));
	}

	public void testGetMovieIncludeActors() throws Exception {
		final String movieId = "bond";
		final Movie movie = fetchMovie(movieId, "include", "actors");

		assertThat(movie.getId(), is(movieId));
		assertThat(movie.getTitle(), is("Agent 007"));
		assertThat(movie.getYear(), is(1960));
		assertThat(movie.getActors(), hasSize(2));

		List<Actor> actors = movie.getActors();
		Actor jane = actors.get(0);
		assertThat(jane.getId(), is("jane"));
		assertThat(jane.getName(), is("Jane Franklin"));
		assertThat(jane.getMovies(), is(nullValue()));

		Actor may = actors.get(1);
		assertThat(may.getId(), is("may"));
		assertThat(may.getName(), is("James May"));
		assertThat(may.getMovies(), is(nullValue()));
	}

	public void testVote() throws Exception {
		final String movieId = "bond";
		Movie movie = fetchMovie(movieId);
		assertThat(movie.getMeta().getAverageStars(), is(4));

		Response response = target("/movie/" + movieId + "/vote").queryParam("stars", "2").request().post(null);
		assertResponseStatus(response, Response.Status.OK);
		assertHeader(response.getHeaders(), HttpHeaders.CONTENT_TYPE, JsonApiMediaType.APPLICATION_JSON_API);
		assertThat(response.readEntity(String.class), is("updated"));

		movie = fetchMovie(movieId);
		assertThat(movie.getMeta().getAverageStars(), is(2));
	}

	private Movie fetchMovie(String movieId) throws Exception {
		return fetchMovie(movieId, null, null);
	}
	private Movie fetchMovie(String movieId, String queryParamKey, String queryParamValue) throws Exception {
		WebTarget target = target("/movie/" + movieId);
		if (queryParamKey != null && queryParamValue != null) {
			target = target.queryParam(queryParamKey, queryParamValue);
		}
		Response response = target.request().get();
		assertResponseStatus(response, Response.Status.OK);
		assertHeader(response.getHeaders(), HttpHeaders.CONTENT_TYPE, JsonApiMediaType.APPLICATION_JSON_API);

		JsonNode content = mapper.readTree((InputStream) response.getEntity());
		JsonNode data = content.get("data");
		JsonNode included = content.get("included");

		Movie movie = getMovieFromJson(data);
		List<Actor> actors = getActorsFromJson(included);
		if (!actors.isEmpty()) {
			movie.setActors(actors);
		}

		return movie;
	}

	private Movie getMovieFromJson(JsonNode node) throws JsonProcessingException {
		if (node.isObject()) {
			ObjectNode onode = (ObjectNode) node;
			final JsonNode type = onode.remove("type");
			final JsonNode attributes = onode.remove("attributes");
			final JsonNode relationships = onode.remove("relationships");
			final JsonNode links = onode.remove("links");
			Iterator<Map.Entry<String, JsonNode>> fields = attributes.fields();
			while(fields.hasNext()) {
				Map.Entry<String, JsonNode> f = fields.next();
				onode.put(f.getKey(), f.getValue().textValue());
			}

			Movie movie = mapper.treeToValue(onode, Movie.class);

			if (attributes != null) {
				movie.setYear(attributes.findValue("year").asInt());
			}

			return movie;
		}
		else {
			throw new JsonMappingException("Not an object: " + node);
		}
	}

	private List<Actor> getActorsFromJson(JsonNode included) throws JsonProcessingException {
		assertThat(included.getNodeType(), is(JsonNodeType.ARRAY));

		List<Actor> actors = new ArrayList<>();
		ArrayNode array = (ArrayNode) included;
		for (JsonNode anode : array) {
			if (anode.isObject()) {
				ObjectNode aonode = (ObjectNode) anode;
				final JsonNode type = aonode.remove("type");
				if (!"actor".equals(type.textValue())) {
					continue;
				}
				final JsonNode attributes = aonode.remove("attributes");
				final JsonNode relationships = aonode.remove("relationships");
				final JsonNode links = aonode.remove("links");
				Actor actor = mapper.treeToValue(aonode, Actor.class);

				if (attributes != null) {
					actor.setName(attributes.findValue("name").asText());
				}

				actors.add(actor);
			}
		}
		return actors;
	}

	private void assertResponseStatus(Response response, Response.Status status) {
		assertThat(response, is(notNullValue()));
		assertThat(Response.Status.fromStatusCode(response.getStatus()), is(status));
	}

	private void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String... headerValues) {
		assertThat(headers, hasKey(headerName));
		final List<Object> values = headers.get(headerName);
		assertThat(values, hasSize(headerValues.length));
	}
}
