package io.katharsis.example.spring.jersey;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class SpringJerseyApplication {

	private static final URI BASE_URI = URI.create("http://localhost:8080/");

	public static void main(String[] args) throws IOException {
		final ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(SpringKatharsisFeature.class);

		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig);
		System.out.println("Press any key to close");
		System.in.read();
		server.shutdownNow();
	}

}
