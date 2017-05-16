package io.katharsis.operations.server.order;

import java.util.List;

import io.katharsis.operations.Operation;

public interface OperationOrderStrategy {

	List<OrderedOperation> order(List<Operation> operations);
}
