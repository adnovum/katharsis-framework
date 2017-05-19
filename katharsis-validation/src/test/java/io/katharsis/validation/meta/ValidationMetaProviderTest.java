package io.katharsis.validation.meta;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.core.boot.KatharsisBoot;
import io.katharsis.core.module.discovery.ReflectionsServiceDiscovery;
import io.katharsis.legacy.locator.SampleJsonServiceLocator;
import io.katharsis.meta.MetaLookup;
import io.katharsis.meta.model.MetaAttribute;
import io.katharsis.meta.model.resource.MetaResourceBase;
import io.katharsis.meta.provider.resource.ResourceMetaProvider;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.rs.internal.JaxrsModule;
import io.katharsis.validation.mock.models.Task;

public class ValidationMetaProviderTest {

	private MetaLookup lookup;

	private void setup(boolean addValidationProvider) {
		KatharsisBoot boot = new KatharsisBoot();
		boot.addModule(new JaxrsModule(null));
		boot.setServiceUrlProvider(new ConstantServiceUrlProvider("http://localhost"));
		boot.setServiceDiscovery(
				new ReflectionsServiceDiscovery("io.katharsis.validation.mock.model", new SampleJsonServiceLocator()));
		boot.boot();

		lookup = new MetaLookup();
		lookup.setModuleContext(boot.getModuleRegistry().getContext());
		lookup.addProvider(new ResourceMetaProvider());
		if (addValidationProvider) {
			lookup.addProvider(new ValidationMetaProvider());
		}
		lookup.initialize();
	}

	@Test
	public void testNotNullNotDisabledWithoutValidationProvider() {
		setup(false);
		MetaResourceBase meta = lookup.getMeta(Task.class, MetaResourceBase.class);
		MetaAttribute attr = meta.getAttribute("name");
		Assert.assertTrue(attr.isNullable());
	}

	@Test
	public void testNotNullDisablesNullablity() {
		setup(true);
		MetaResourceBase meta = lookup.getMeta(Task.class, MetaResourceBase.class);
		MetaAttribute attr = meta.getAttribute("name");
		Assert.assertFalse(attr.isNullable());
	}
}
