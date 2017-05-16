package io.katharsis.operations.server;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.katharsis.operations.internal.PATCH;

/**
 * Allows to execute multiple requests with a single using http://jsonpatch.com/.
 */
@Path("/operations")
public interface OperationsService {

	@PATCH
	@Path("/")
	@Consumes("application/json-patch+json")
	@Produces("application/json-patch+json")
	Response patch(String operations) throws IOException;

}
