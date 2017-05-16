package io.katharsis.operations.server;

import java.util.List;

import io.katharsis.operations.OperationResponse;


public interface OperationFilter {

	List<OperationResponse> filter(OperationFilterContext context, OperationFilterChain chain);

}
