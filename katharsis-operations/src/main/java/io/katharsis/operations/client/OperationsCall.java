package io.katharsis.operations.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.katharsis.client.KatharsisClient;
import io.katharsis.client.http.HttpAdapter;
import io.katharsis.client.http.HttpAdapterRequest;
import io.katharsis.client.http.HttpAdapterResponse;
import io.katharsis.client.internal.ClientDocumentMapper;
import io.katharsis.core.internal.query.QuerySpecAdapter;
import io.katharsis.core.internal.resource.DocumentMapper;
import io.katharsis.errorhandling.exception.InternalServerErrorException;
import io.katharsis.operations.Operation;
import io.katharsis.operations.OperationResponse;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.request.HttpMethod;
import io.katharsis.repository.request.QueryAdapter;
import io.katharsis.repository.response.JsonApiResponse;
import io.katharsis.resource.Document;
import io.katharsis.resource.Resource;

public class OperationsCall {

	private OperationsClient client;

	private List<QueuedOperation> queuedOperations = new ArrayList<>();

	private List<OperationResponse> responses;

	protected OperationsCall(OperationsClient client) {
		this.client = client;
	}

	public void add(HttpMethod method, Object object) {
		Operation operation = new Operation();

		Resource resource = toResource(object);

		operation.setOp(method.toString());
		operation.setPath(computePath(method, resource));
		if (method == HttpMethod.POST || method == HttpMethod.PATCH) {
			operation.setValue(resource);
		}

		QueuedOperation queuedOperation = new QueuedOperation();
		queuedOperation.resourceClass = resource.getClass();
		queuedOperation.operation = operation;
		queuedOperations.add(queuedOperation);
	}

	protected String computePath(HttpMethod method, Resource resource) {
		if (method == HttpMethod.POST) {
			return resource.getType();
		}
		return resource.getType() + "/" + resource.getId() + "/";
	}

	protected Resource toResource(Object object) {
		JsonApiResponse response = new JsonApiResponse();
		response.setEntity(object);

		QuerySpec querySpec = new QuerySpec(object.getClass());
		QueryAdapter queryAdapter = new QuerySpecAdapter(querySpec, client.getKatharsis().getRegistry());

		KatharsisClient katharsis = client.getKatharsis();
		DocumentMapper documentMapper = katharsis.getDocumentMapper();
		Document document = documentMapper.toDocument(response, queryAdapter);
		return document.getSingleData().get();
	}

	protected <T> T fromResource(Document document, Class<T> clazz) {
		KatharsisClient katharsis = client.getKatharsis();
		ClientDocumentMapper documentMapper = katharsis.getDocumentMapper();
		return (T) documentMapper.fromDocument(document, false);
	}

	public void execute() {
		List<Operation> operations = new ArrayList<>();
		for(QueuedOperation queuedOperation : queuedOperations){
			operations.add(queuedOperation.operation);
		}

		HttpAdapter adapter = client.getKatharsis().getHttpAdapter();
		ObjectMapper mapper = client.getKatharsis().getObjectMapper();
		try {
			String operationsJson = mapper.writer().writeValueAsString(operations.toArray(new Operation[operations.size()]));

			String url = client.getKatharsis().getRegistry().getServiceUrlProvider().getUrl() + "/operations";
			HttpAdapterRequest request = adapter.newRequest(url, HttpMethod.PATCH, operationsJson);
			HttpAdapterResponse response = request.execute();

			int status = response.code();
			if (status != 200) {
				// general issue, status of individual operations is important.
				throw new InternalServerErrorException("patch execution failed with status " + status);
			}
			String json = response.body();
			responses = Arrays.asList(mapper.readValue(json, OperationResponse[].class));
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public OperationResponse getResponse(int index) {
		checkResponsesAvailable();
		return responses.get(index);
	}

	public <T> T getResponseObject(int index, Class<T> clazz) {
		OperationResponse response = responses.get(index);
		return fromResource(response, clazz);
	}

	private void checkResponsesAvailable() {
		if (responses == null) {
			throw new IllegalStateException("response not yet available, wait for execute() to finish");
		}
	}

	private class QueuedOperation {

		private Operation operation;

		private Class<?> resourceClass;

	}
}
