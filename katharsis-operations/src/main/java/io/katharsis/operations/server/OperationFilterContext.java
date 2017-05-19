package io.katharsis.operations.server;

import java.util.List;

import io.katharsis.operations.server.order.OrderedOperation;
import io.katharsis.core.module.discovery.ServiceDiscovery;

public interface OperationFilterContext {

	List<OrderedOperation> getOrderedOperations();

	ServiceDiscovery getServiceDiscovery();
}
