package io.katharsis.operations.server.order;

import io.katharsis.operations.Operation;

public class OrderedOperation {

	private Operation operation;

	private int ordinal;

	public OrderedOperation(Operation operation, int ordinal) {
		this.operation = operation;
		this.ordinal = ordinal;
	}

	public Operation getOperation() {
		return operation;
	}

	public int getOrdinal() {
		return ordinal;
	}
}
