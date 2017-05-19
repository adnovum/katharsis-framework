package io.katharsis.core.resource;

import io.katharsis.core.engine.document.ResourceIdentifier;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ResourceEqualsContractTest {

	@Test
	public void testResourceIdEqualsContract() throws NoSuchFieldException {
		EqualsVerifier.forClass(ResourceIdentifier.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
