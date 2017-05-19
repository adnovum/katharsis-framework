package io.katharsis.client;

import io.katharsis.test.mock.TestExceptionMapper;
import io.katharsis.core.module.SimpleModule;

public class TestModule extends SimpleModule {

	public TestModule() {
		super("test");
		
		addExceptionMapper(new TestExceptionMapper());
	}

}
