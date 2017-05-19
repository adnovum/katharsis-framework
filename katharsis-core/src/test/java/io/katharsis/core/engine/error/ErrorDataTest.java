package io.katharsis.core.engine.error;

import io.katharsis.core.engine.document.ErrorData;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ErrorDataTest {

    @Test
    public void shouldFulfillEqualsHashCodeContract() throws Exception {
        EqualsVerifier.forClass(ErrorData.class).allFieldsShouldBeUsed().verify();
    }

}