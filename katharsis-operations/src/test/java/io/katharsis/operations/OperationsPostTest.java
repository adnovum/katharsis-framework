package io.katharsis.operations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import io.katharsis.operations.client.OperationsCall;
import io.katharsis.operations.client.OperationsClient;
import io.katharsis.operations.model.MovieEntity;
import io.katharsis.operations.model.PersonEntity;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.resource.list.ResourceList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OperationsPostTest extends AbstractOperationsTest {

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
	public void testMultiplePost() {
		ResourceRepositoryV2<PersonEntity, UUID> personRepo = client.getRepositoryForType(PersonEntity.class);

		PersonEntity person1 = newPerson("1");
		PersonEntity person2 = newPerson("2");
		MovieEntity movie = newMovie("test");
		movie.setDirectors(new HashSet<>(Arrays.asList(person1, person2)));

		OperationsCall call = operationsClient.createCall();
		call.add(HttpMethod.POST, movie);
		call.add(HttpMethod.POST, person1);
		call.add(HttpMethod.POST, person2);
		call.execute();


		QuerySpec querySpec = new QuerySpec(PersonEntity.class);
		ResourceList<PersonEntity> persons = personRepo.findAll(querySpec);
		Assert.assertEquals(2, persons.size());

		querySpec = new QuerySpec(MovieEntity.class);
		querySpec.includeRelation(Arrays.asList("directors"));
		ResourceList<MovieEntity> movies = movieRepo.findAll(querySpec);
		Assert.assertEquals(1, movies.size());
		movie = movies.get(0);
		Assert.assertEquals(2, movie.getDirectors().size());
	}
}
