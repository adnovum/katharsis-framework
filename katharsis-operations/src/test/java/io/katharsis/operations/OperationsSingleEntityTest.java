package io.katharsis.operations;

import java.util.UUID;

import io.katharsis.operations.client.OperationsCall;
import io.katharsis.operations.client.OperationsClient;
import io.katharsis.operations.model.MovieEntity;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.ResourceRepositoryV2;
import io.katharsis.repository.request.HttpMethod;
import io.katharsis.resource.list.ResourceList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OperationsSingleEntityTest extends AbstractOperationsTest {

	protected ResourceRepositoryV2<MovieEntity, UUID> movieRepo;

	private OperationsClient operationsClient;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		movieRepo = client.getRepositoryForType(MovieEntity.class);
		operationsClient = new OperationsClient(client);
	}

	@Test
	public void testSingleEntityCrud() {
		MovieEntity movie = newMovie("test");

		// post
		OperationsCall call = operationsClient.createCall();
		call.add(HttpMethod.POST, movie);
		call.execute();

		// read
		ResourceList<MovieEntity> movies = movieRepo.findAll(new QuerySpec(MovieEntity.class));
		Assert.assertEquals(1, movies.size());
		movie = movies.get(0);


		// update
		movie.setTitle("NewTitle");
		call = operationsClient.createCall();
		call.add(HttpMethod.PATCH, movie);
		call.execute();
		movie = call.getResponseObject(0, MovieEntity.class);
		Assert.assertEquals("NewTitle", movie.getTitle());

		// delete
		call = operationsClient.createCall();
		call.add(HttpMethod.DELETE, movie);
		call.execute();

		movies = movieRepo.findAll(new QuerySpec(MovieEntity.class));
		Assert.assertEquals(0, movies.size());
	}
}
