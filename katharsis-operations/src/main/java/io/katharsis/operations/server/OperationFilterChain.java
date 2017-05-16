package io.katharsis.operations.server;

import java.util.List;

import io.katharsis.operations.OperationResponse;

public interface OperationFilterChain {

	List<OperationResponse> doFilter(OperationFilterContext context);

}
