package io.katharsis.operations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.katharsis.operations.client.OperationsCall;
import io.katharsis.operations.client.OperationsClient;
import io.katharsis.operations.model.MovieEntity;
import io.katharsis.operations.model.PersonEntity;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.engine.http.HttpMethod;
import io.katharsis.core.engine.document.Relationship;
import io.katharsis.core.engine.document.Resource;
import io.katharsis.core.engine.document.ResourceIdentifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OperationsDeleteRelationTest extends AbstractOperationsTest {

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
	public void testDeleteRelation() {
		PersonEntity person1 = newPerson("1");
		PersonEntity person2 = newPerson("2");
		MovieEntity movie = newMovie("test");
		movie.setDirectors(new HashSet<>(Arrays.asList(person1, person2)));
		OperationsCall insertCall = operationsClient.createCall();
		insertCall.add(HttpMethod.POST, movie);
		insertCall.add(HttpMethod.POST, person1);
		insertCall.add(HttpMethod.POST, person2);
		insertCall.execute();

		QuerySpec querySpec = new QuerySpec(MovieEntity.class);
		querySpec.includeRelation(Arrays.asList("directors"));
		MovieEntity updatedMovie = movieRepo.findOne(movie.getId(), querySpec);
		Set<PersonEntity> directors = updatedMovie.getDirectors();
		PersonEntity deletedDirector = directors.iterator().next();
		directors.remove(deletedDirector);

		OperationsCall call = operationsClient.createCall();
		call.add(HttpMethod.PATCH, updatedMovie);
		call.add(HttpMethod.DELETE, deletedDirector);
		call.execute();

		// check whether updated relationship is included in the response
		Resource movieResource = call.getResponse(0).getSingleData().get();
		Relationship directorsRelationship = movieResource.getRelationships().get("directors");
		List<ResourceIdentifier> directorIds = directorsRelationship.getCollectionData().get();
		Assert.assertEquals(1, directorIds.size());
	}
}
