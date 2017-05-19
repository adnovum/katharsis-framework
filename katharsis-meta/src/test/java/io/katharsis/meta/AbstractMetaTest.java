package io.katharsis.meta;

import org.junit.Before;

import io.katharsis.core.boot.KatharsisBoot;
import io.katharsis.core.module.discovery.ReflectionsServiceDiscovery;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.rs.internal.JaxrsModule;

public class AbstractMetaTest {

	protected KatharsisBoot boot;

	@Before
	public void setup() {
		boot = new KatharsisBoot();
		boot.addModule(new JaxrsModule(null));
		boot.setServiceUrlProvider(new ConstantServiceUrlProvider("http://localhost"));
		boot.setServiceDiscovery(new ReflectionsServiceDiscovery("io.katharsis.meta.mock.model", new SampleJsonServiceLocator()));
		configure();
		boot.boot();
	}

	protected void configure() {

	}
}
