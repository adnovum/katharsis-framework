//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.katharsis.operations.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.katharsis.core.module.Module;
import io.katharsis.operations.server.order.DependencyOrderStrategy;
import io.katharsis.operations.server.order.OperationOrderStrategy;

public class OperationsModule implements Module {

	private OperationOrderStrategy orderStrategy = new DependencyOrderStrategy();

	private List<OperationFilter> filters = new CopyOnWriteArrayList<>();

	public void addFilter(OperationFilter filter) {
		this.filters.add(filter);
	}

	public void removeFilter(OperationFilter filter) {
		this.filters.remove(filter);
	}

	public OperationOrderStrategy getOrderStrategy() {
		return orderStrategy;
	}

	public void setOrderStrategy(OperationOrderStrategy orderStrategy) {
		this.orderStrategy = orderStrategy;
	}

	public List<OperationFilter> getFilters() {
		return filters;
	}

	@Override
	public String getModuleName() {
		return "operations";
	}

	@Override
	public void setupModule(ModuleContext context) {
		context.addHttpRequestProcessor(new OperationsRequestProcessor(this, context));
	}
}
