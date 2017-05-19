package io.katharsis.core.engine.error;

import io.katharsis.core.engine.error.ErrorResponse;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ErrorResponseTest {

    @Test
    public void shouldFulfillHashcodeEqualsContract() throws Exception {
        EqualsVerifier.forClass(ErrorResponse.class).verify();
    }
}