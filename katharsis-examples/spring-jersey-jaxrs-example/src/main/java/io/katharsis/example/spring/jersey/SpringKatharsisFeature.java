package io.katharsis.example.spring.jersey;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;

import org.springframework.context.ApplicationContext;

import io.katharsis.internal.boot.KatharsisBoot;
import io.katharsis.rs.KatharsisFeature;
import io.katharsis.spring.internal.SpringServiceDiscovery;

@ConstrainedTo(RuntimeType.SERVER)
public class SpringKatharsisFeature extends KatharsisFeature {

	private KatharsisBoot boot = new KatharsisBoot();

	@Context
	private ApplicationContext applicationContext;

	@Override
	public boolean configure(final FeatureContext context) {
		// Set SpringServiceDiscovery for Katharsis to pick up the repositories defined as Spring beans
		SpringServiceDiscovery springServiceDiscovery = new SpringServiceDiscovery();
		springServiceDiscovery.setApplicationContext(applicationContext);
		boot.setServiceDiscovery(springServiceDiscovery);
		setServiceDiscover(springServiceDiscovery);
		return super.configure(context);
	}
}
