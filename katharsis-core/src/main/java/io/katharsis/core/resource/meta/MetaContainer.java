package io.katharsis.core.resource.meta;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface MetaContainer {

	public ObjectNode getMeta();

	public void setMeta(ObjectNode meta);
}
