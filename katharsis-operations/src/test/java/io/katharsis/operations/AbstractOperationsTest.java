package io.katharsis.operations;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.katharsis.client.KatharsisClient;
import io.katharsis.client.action.JerseyActionStubFactory;
import io.katharsis.client.http.okhttp.OkHttpAdapter;
import io.katharsis.client.http.okhttp.OkHttpAdapterListenerBase;
import io.katharsis.jpa.JpaModule;
import io.katharsis.jpa.JpaRepositoryConfig;
import io.katharsis.jpa.meta.JpaMetaProvider;
import io.katharsis.jpa.query.JpaQueryFactory;
import io.katharsis.jpa.query.JpaQueryFactoryContext;
import io.katharsis.jpa.query.criteria.JpaCriteriaQueryFactory;
import io.katharsis.meta.MetaLookup;
import io.katharsis.meta.MetaModule;
import io.katharsis.meta.provider.resource.ResourceMetaProvider;
import io.katharsis.operations.model.MovieEntity;
import io.katharsis.operations.model.PersonEntity;
import io.katharsis.operations.server.OperationsModule;
import io.katharsis.operations.server.TransactionOperationFilter;
import io.katharsis.rs.KatharsisFeature;
import io.katharsis.spring.internal.SpringServiceDiscovery;
import io.katharsis.spring.jpa.SpringTransactionRunner;
import okhttp3.OkHttpClient.Builder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractOperationsTest extends JerseyTest {

	protected KatharsisClient client;

	protected AnnotationConfigApplicationContext context;

	@Before
	public void setup() {
		client = new KatharsisClient(getBaseUri().toString());
		client.setActionStubFactory(JerseyActionStubFactory.newInstance());
		client.getHttpAdapter().setReceiveTimeout(10000000, TimeUnit.MILLISECONDS);

		MetaModule metaModule = MetaModule.create();
		metaModule.addMetaProvider(new ResourceMetaProvider());
		client.addModule(metaModule);

		JpaModule module = JpaModule.newClientModule();
		setupModule(module, false);
		client.addModule(module);

		setNetworkTimeout(client, 10000, TimeUnit.SECONDS);
	}

	protected MovieEntity newMovie(String title) {
		MovieEntity movie = new MovieEntity();
		movie.setId(UUID.randomUUID());
		movie.setImdbId(title);
		movie.setTitle(title);
		return movie;
	}

	protected PersonEntity newPerson(String name) {
		PersonEntity person = new PersonEntity();
		person.setId(UUID.randomUUID());
		person.setName(name);
		return person;
	}

	public static void setNetworkTimeout(KatharsisClient client, final int timeout, final TimeUnit timeUnit) {
		OkHttpAdapter httpAdapter = (OkHttpAdapter) client.getHttpAdapter();
		httpAdapter.addListener(new OkHttpAdapterListenerBase() {

			@Override
			public void onBuild(Builder builder) {
				builder.readTimeout(timeout, timeUnit);
			}
		});
	}

	protected void setupModule(JpaModule module, boolean server) {
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		SpringTransactionRunner transactionRunner = context.getBean(SpringTransactionRunner.class);
		transactionRunner.doInTransaction(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				EntityManager em = context.getBean(EntityManagerProducer.class).getEntityManager();
				clear(em);
				return null;
			}
		});

		if (context != null) {
			context.destroy();
		}
	}


	public static void clear(EntityManager em) {
		clear(em, JpaCriteriaQueryFactory.newInstance());
	}

	public static void clear(final EntityManager em, JpaQueryFactory factory) {
		factory.initalize(new JpaQueryFactoryContext() {
			@Override
			public MetaLookup getMetaLookup() {
				MetaLookup metaLookup = new MetaLookup();
				metaLookup.addProvider(new JpaMetaProvider());
				return metaLookup;
			}

			@Override
			public EntityManager getEntityManager() {
				return em;
			}
		});
		clear(em, factory.query(MovieEntity.class).buildExecutor().getResultList());
		clear(em, factory.query(PersonEntity.class).buildExecutor().getResultList());
		em.flush();
		em.clear();
	}

	private static void clear(EntityManager em, List<?> list) {
		for (Object obj : list) {
			em.remove(obj);
		}
	}

	@Override
	protected Application configure() {
		return new TestApplication();
	}

	@ApplicationPath("/")
	private class TestApplication extends ResourceConfig {

		public TestApplication() {
			Assert.assertNull(context);

			context = new AnnotationConfigApplicationContext(OperationsTestConfig.class);
			context.start();
			EntityManagerFactory emFactory = context.getBean(EntityManagerFactory.class);
			EntityManager em = context.getBean(EntityManagerProducer.class).getEntityManager();
			SpringServiceDiscovery serviceDiscovery = context.getBean(SpringServiceDiscovery.class);
			SpringTransactionRunner transactionRunner = context.getBean(SpringTransactionRunner.class);

			KatharsisFeature feature = new KatharsisFeature();
			feature.getBoot().setServiceDiscovery(serviceDiscovery);

			JpaModule jpaModule = JpaModule.newServerModule(em, transactionRunner);
			setupModule(jpaModule, true);

			Set<ManagedType<?>> managedTypes = emFactory.getMetamodel().getManagedTypes();
			for (ManagedType<?> managedType : managedTypes) {
				Class<?> managedJavaType = managedType.getJavaType();
				if (managedJavaType.getAnnotation(Entity.class) != null) {
					if (!jpaModule.hasRepository(managedJavaType)) {
						jpaModule.addRepository(JpaRepositoryConfig.builder(managedJavaType).build());
					}
				}
			}

			OperationsModule operationsModule = new OperationsModule();
			operationsModule.addFilter(new TransactionOperationFilter());

			feature.addModule(jpaModule);
			feature.addModule(operationsModule);
			register(feature);
		}
	}

}
