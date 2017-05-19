package io.katharsis.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.katharsis.core.boot.KatharsisBoot;
import io.katharsis.core.engine.internal.exception.ExceptionMapperRegistry;
import io.katharsis.core.engine.document.ErrorData;
import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.exception.InternalServerErrorException;
import io.katharsis.core.engine.error.JsonApiExceptionMapper;
import io.katharsis.core.engine.document.Document;
import io.katharsis.rs.type.JsonApiMediaType;
import io.katharsis.core.utils.Optional;

/**
 * Allows to return JAXRS exceptions in the JSON API format.
 */
public class JsonapiExceptionMapperBridge implements ExceptionMapper<RuntimeException> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonapiExceptionMapperBridge.class);

	private KatharsisFeature feature;

	public JsonapiExceptionMapperBridge(KatharsisFeature feature) {
		this.feature = feature;
	}

	@Override
	public Response toResponse(RuntimeException exception) {
		KatharsisBoot boot = this.feature.getBoot();
		ExceptionMapperRegistry exceptionMapperRegistry = boot.getExceptionMapperRegistry();
		Optional<JsonApiExceptionMapper> optional = exceptionMapperRegistry.findMapperFor(exception.getClass());

		if (!optional.isPresent()) {
			LOGGER.error("no exception mapper found", exception);
			exception = new InternalServerErrorException(exception.getMessage());
		}
		JsonApiExceptionMapper exceptionMapper = optional.get();
		ErrorResponse errorResponse = exceptionMapper.toErrorResponse(exception);

		// use the Katharsis document mapper to create a JSON API response
		Document doc = new Document();

		List<ErrorData> errors = new ArrayList<>();
		for (ErrorData error : errorResponse.getErrors()) {
			errors.add(error);
		}
		doc.setErrors(errors);

		return Response.status(errorResponse.getHttpStatus()).entity(doc).header("Content-Type", JsonApiMediaType.APPLICATION_JSON_API).build();
	}

}
