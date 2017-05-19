package io.katharsis.core.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.katharsis.core.module.discovery.ServiceDiscovery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.core.engine.internal.dispatcher.filter.TestFilter;
import io.katharsis.core.engine.internal.dispatcher.filter.TestRepositoryDecorator;
import io.katharsis.core.engine.internal.dispatcher.filter.TestRepositoryDecorator.DecoratedScheduleRepository;
import io.katharsis.core.engine.internal.exception.ExceptionMapperLookup;
import io.katharsis.core.engine.internal.exception.ExceptionMapperRegistryTest.IllegalStateExceptionMapper;
import io.katharsis.core.engine.internal.exception.ExceptionMapperRegistryTest.SomeIllegalStateExceptionMapper;
import io.katharsis.core.engine.internal.registry.DirectResponseRelationshipEntry;
import io.katharsis.core.engine.internal.registry.ResourceRegistryImpl;
import io.katharsis.core.engine.error.JsonApiExceptionMapper;
import io.katharsis.legacy.registry.DefaultResourceInformationBuilderContext;
import io.katharsis.core.queryspec.QuerySpec;
import io.katharsis.core.repository.RelationshipRepositoryV2;
import io.katharsis.core.repository.ResourceRepositoryV2;
import io.katharsis.core.repository.decorate.RepositoryDecoratorFactory;
import io.katharsis.core.engine.filter.DocumentFilter;
import io.katharsis.core.engine.information.repository.RelationshipRepositoryInformation;
import io.katharsis.core.engine.information.repository.RepositoryInformationBuilder;
import io.katharsis.core.engine.information.repository.RepositoryInformationBuilderContext;
import io.katharsis.core.engine.information.repository.ResourceRepositoryInformation;
import io.katharsis.core.resource.annotations.JsonApiId;
import io.katharsis.core.resource.annotations.JsonApiResource;
import io.katharsis.core.engine.information.resource.ResourceFieldNameTransformer;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.engine.information.resource.ResourceInformationBuilder;
import io.katharsis.core.resource.list.ResourceList;
import io.katharsis.core.mock.models.ComplexPojo;
import io.katharsis.core.mock.models.Document;
import io.katharsis.core.mock.models.FancyProject;
import io.katharsis.core.mock.models.Project;
import io.katharsis.core.mock.models.Schedule;
import io.katharsis.core.mock.models.Task;
import io.katharsis.core.mock.models.Thing;
import io.katharsis.core.mock.models.User;
import io.katharsis.core.mock.repository.DocumentRepository;
import io.katharsis.core.mock.repository.PojoRepository;
import io.katharsis.core.mock.repository.ProjectRepository;
import io.katharsis.core.mock.repository.ResourceWithoutRepositoryToProjectRepository;
import io.katharsis.core.mock.repository.ScheduleRepository;
import io.katharsis.core.mock.repository.ScheduleRepositoryImpl;
import io.katharsis.core.mock.repository.TaskRepository;
import io.katharsis.core.mock.repository.TaskToProjectRepository;
import io.katharsis.core.mock.repository.TaskWithLookupRepository;
import io.katharsis.core.mock.repository.UserRepository;
import io.katharsis.core.mock.repository.UserToProjectRepository;
import io.katharsis.core.engine.url.ConstantServiceUrlProvider;
import io.katharsis.core.engine.registry.RegistryEntry;
import io.katharsis.core.module.discovery.ResourceLookup;
import io.katharsis.core.engine.registry.ResourceRegistry;
import io.katharsis.core.engine.security.SecurityProvider;
import io.katharsis.core.engine.parser.TypeParser;

public class ModuleTest {

	private ResourceRegistry resourceRegistry;

	private ModuleRegistry moduleRegistry;

	private TestModule testModule;

	private ServiceDiscovery serviceDiscovery = Mockito.mock(ServiceDiscovery.class);

	@Before
	public void setup() {
		moduleRegistry = new ModuleRegistry();
		resourceRegistry = new ResourceRegistryImpl(moduleRegistry, new ConstantServiceUrlProvider("http://localhost"));

		testModule = new TestModule();
		moduleRegistry.addModule(new CoreModule("io.katharsis.core.module.mock", new ResourceFieldNameTransformer()));
		moduleRegistry.addModule(testModule);
		moduleRegistry.setServiceDiscovery(serviceDiscovery);
		moduleRegistry.init(new ObjectMapper());

		Assert.assertEquals(resourceRegistry, moduleRegistry.getResourceRegistry());
	}

	@Test
	public void getModules() {
		Assert.assertEquals(2, moduleRegistry.getModules().size());
	}

	@Test
	public void testGetServiceDiscovery() {
		Assert.assertEquals(serviceDiscovery, moduleRegistry.getServiceDiscovery());
		Assert.assertEquals(serviceDiscovery, testModule.context.getServiceDiscovery());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void invalidRepository() {
		moduleRegistry.getRepositoryInformationBuilder().build("no resource", null);
	}

	@Test
	public void repositoryInformationBuilderAccept() {
		RepositoryInformationBuilder builder = moduleRegistry.getRepositoryInformationBuilder();
		Assert.assertFalse(builder.accept("no resource"));
		Assert.assertTrue(builder.accept(TaskRepository.class));
		Assert.assertTrue(builder.accept(ProjectRepository.class));
		Assert.assertTrue(builder.accept(TaskToProjectRepository.class));
		Assert.assertTrue(builder.accept(new TaskRepository()));
		Assert.assertTrue(builder.accept(new TaskToProjectRepository()));
	}

	@Test
	public void buildResourceRepositoryInformationFromClass() {
		RepositoryInformationBuilder builder = moduleRegistry.getRepositoryInformationBuilder();

		ResourceRepositoryInformation info = (ResourceRepositoryInformation) builder.build(TaskRepository.class, newRepositoryInformationBuilderContext());
		Assert.assertEquals(TaskRepository.class, info.getRepositoryClass());
		Assert.assertEquals(Task.class, info.getResourceInformation().getResourceClass());
		Assert.assertEquals("tasks", info.getPath());
	}

	@Test
	public void buildResourceRepositoryInformationFromInstance() {
		RepositoryInformationBuilder builder = moduleRegistry.getRepositoryInformationBuilder();

		ResourceRepositoryInformation info = (ResourceRepositoryInformation) builder.build(new TaskRepository(), newRepositoryInformationBuilderContext());
		Assert.assertEquals(TaskRepository.class, info.getRepositoryClass());
		Assert.assertEquals(Task.class, info.getResourceInformation().getResourceClass());
		Assert.assertEquals("tasks", info.getPath());
	}

	@Test
	public void buildRelationshipRepositoryInformationFromClass() {
		RepositoryInformationBuilder builder = moduleRegistry.getRepositoryInformationBuilder();

		RelationshipRepositoryInformation info = (RelationshipRepositoryInformation) builder.build(TaskToProjectRepository.class, newRepositoryInformationBuilderContext());
		Assert.assertEquals(TaskToProjectRepository.class, info.getRepositoryClass());
		Assert.assertEquals(Project.class, info.getResourceInformation().getResourceClass());
		Assert.assertEquals(Task.class, info.getSourceResourceInformation().getResourceClass());
	}

	@Test
	public void buildRelationshipRepositoryInformationFromInstance() {
		RepositoryInformationBuilder builder = moduleRegistry.getRepositoryInformationBuilder();

		RelationshipRepositoryInformation info = (RelationshipRepositoryInformation) builder.build(new TaskToProjectRepository(), newRepositoryInformationBuilderContext());
		Assert.assertEquals(TaskToProjectRepository.class, info.getRepositoryClass());
		Assert.assertEquals(Project.class, info.getResourceInformation().getResourceClass());
		Assert.assertEquals(Task.class, info.getSourceResourceInformation().getResourceClass());
	}

	private RepositoryInformationBuilderContext newRepositoryInformationBuilderContext() {
		return new RepositoryInformationBuilderContext() {

			@Override
			public ResourceInformationBuilder getResourceInformationBuilder() {
				return moduleRegistry.getResourceInformationBuilder();
			}

			@Override
			public TypeParser getTypeParser() {
				return moduleRegistry.getTypeParser();
			}
		};
	}

	@Test(expected = IllegalStateException.class)
	public void testNotInitialized() {
		moduleRegistry = new ModuleRegistry();
		moduleRegistry.getResourceRegistry();
	}

	@Test
	public void testExceptionMappers() {
		ExceptionMapperLookup exceptionMapperLookup = moduleRegistry.getExceptionMapperLookup();
		Set<JsonApiExceptionMapper> exceptionMappers = exceptionMapperLookup.getExceptionMappers();
		Set<Class<?>> classes = new HashSet<>();
		for (JsonApiExceptionMapper exceptionMapper : exceptionMappers) {
			classes.add(exceptionMapper.getClass());
		}
		Assert.assertTrue(classes.contains(IllegalStateExceptionMapper.class));
		Assert.assertTrue(classes.contains(SomeIllegalStateExceptionMapper.class));
	}

	@Test
	public void testInitCalled() {
		Assert.assertTrue(testModule.initialized);
	}

	@Test(expected = IllegalStateException.class)
	public void testModuleChangeAfterAddModule() {
		SimpleModule module = new SimpleModule("test2");
		moduleRegistry.addModule(module);
		module.addFilter(new TestFilter());
	}

	@Test(expected = IllegalStateException.class)
	public void testContextChangeAfterAddModule() {
		testModule.getContext().addFilter(new TestFilter());
	}

	@Test
	public void testGetResourceRegistry() {
		Assert.assertSame(resourceRegistry, testModule.getContext().getResourceRegistry());
	}

	@Test(expected = IllegalStateException.class)
	public void testNoResourceRegistryBeforeInitialization() {
		ModuleRegistry registry = new ModuleRegistry();
		registry.addModule(new SimpleModule("test") {

			@Override
			public void setupModule(ModuleContext context) {
				context.getResourceRegistry(); // fail
			}
		});
	}

	@Test
	public void testInformationBuilder() throws Exception {
		ResourceInformationBuilder informationBuilder = moduleRegistry.getResourceInformationBuilder();

		Assert.assertTrue(informationBuilder.accept(ComplexPojo.class));
		Assert.assertTrue(informationBuilder.accept(Document.class));
		Assert.assertTrue(informationBuilder.accept(FancyProject.class));
		Assert.assertTrue(informationBuilder.accept(Project.class));
		Assert.assertTrue(informationBuilder.accept(Task.class));
		Assert.assertTrue(informationBuilder.accept(Thing.class));
		Assert.assertTrue(informationBuilder.accept(User.class));
		Assert.assertTrue(informationBuilder.accept(TestResource.class));

		Assert.assertFalse(informationBuilder.accept(TestRepository.class));
		Assert.assertFalse(informationBuilder.accept(DocumentRepository.class));
		Assert.assertFalse(informationBuilder.accept(PojoRepository.class));
		Assert.assertFalse(informationBuilder.accept(ProjectRepository.class));
		Assert.assertFalse(informationBuilder.accept(ResourceWithoutRepositoryToProjectRepository.class));
		Assert.assertFalse(informationBuilder.accept(TaskToProjectRepository.class));
		Assert.assertFalse(informationBuilder.accept(TaskWithLookupRepository.class));
		Assert.assertFalse(informationBuilder.accept(UserRepository.class));
		Assert.assertFalse(informationBuilder.accept(UserToProjectRepository.class));

		Assert.assertFalse(informationBuilder.accept(Object.class));
		Assert.assertFalse(informationBuilder.accept(String.class));

		try {
			informationBuilder.build(Object.class);
			Assert.fail();
		} catch (UnsupportedOperationException e) {
			// ok
		}

		DefaultResourceInformationBuilderContext context = new DefaultResourceInformationBuilderContext(informationBuilder, moduleRegistry.getTypeParser());

		ResourceInformation userInfo = informationBuilder.build(User.class);
		Assert.assertEquals("id", userInfo.getIdField().getUnderlyingName());

		ResourceInformation testInfo = informationBuilder.build(TestResource.class);
		Assert.assertEquals("id", testInfo.getIdField().getUnderlyingName());
		Assert.assertEquals("id", testInfo.getIdField().getJsonName());
	}

	@Test
	public void testResourceLookup() throws Exception {
		ResourceLookup resourceLookup = moduleRegistry.getResourceLookup();

		Assert.assertFalse(resourceLookup.getResourceClasses().contains(Object.class));
		Assert.assertFalse(resourceLookup.getResourceClasses().contains(String.class));
		Assert.assertTrue(resourceLookup.getResourceClasses().contains(TestResource.class));

		Assert.assertFalse(resourceLookup.getResourceRepositoryClasses().contains(Object.class));
		Assert.assertFalse(resourceLookup.getResourceRepositoryClasses().contains(String.class));
		Assert.assertTrue(resourceLookup.getResourceRepositoryClasses().contains(TestRepository.class));
	}

	@Test
	public void testJacksonModule() throws Exception {
		List<com.fasterxml.jackson.databind.Module> jacksonModules = moduleRegistry.getJacksonModules();
		Assert.assertEquals(1, jacksonModules.size());
		com.fasterxml.jackson.databind.Module jacksonModule = jacksonModules.get(0);
		Assert.assertEquals("test", jacksonModule.getModuleName());
	}

	@Test
	public void testFilter() throws Exception {
		List<DocumentFilter> filters = moduleRegistry.getFilters();
		Assert.assertEquals(1, filters.size());
	}

	@Test
	public void testDecorators() throws Exception {
		List<RepositoryDecoratorFactory> decorators = moduleRegistry.getRepositoryDecoratorFactories();
		Assert.assertEquals(1, decorators.size());

		RegistryEntry entry = this.resourceRegistry.findEntry(Schedule.class);
		Object resourceRepository = entry.getResourceRepository(null).getResourceRepository();
		Assert.assertNotNull(resourceRepository);
		Assert.assertTrue(resourceRepository instanceof ScheduleRepository);
		Assert.assertTrue(resourceRepository instanceof DecoratedScheduleRepository);
	}

	@Test
	public void testSecurityProvider() throws Exception {
		Assert.assertTrue(moduleRegistry.getSecurityProvider().isUserInRole("testRole"));
		Assert.assertFalse(moduleRegistry.getSecurityProvider().isUserInRole("nonExistingRole"));
		Assert.assertTrue(testModule.getContext().getSecurityProvider().isUserInRole("testRole"));
	}

	@Test
	public void testRepositoryRegistration() {
		RegistryEntry entry = resourceRegistry.findEntry(TestResource2.class);
		ResourceInformation info = entry.getResourceInformation();
		Assert.assertEquals(TestResource2.class, info.getResourceClass());

		Assert.assertNotNull(entry.getResourceRepository(null));
		List<?> relationshipEntries = entry.getRelationshipEntries();
		Assert.assertEquals(1, relationshipEntries.size());
		DirectResponseRelationshipEntry responseRelationshipEntry = (DirectResponseRelationshipEntry) relationshipEntries.get(0);
		Assert.assertNotNull(responseRelationshipEntry);
	}

	class TestModule implements InitializingModule {

		private ModuleContext context;

		private boolean initialized;

		@Override
		public String getModuleName() {
			return "test";
		}

		public ModuleContext getContext() {
			return context;
		}

		@Override
		public void setupModule(ModuleContext context) {
			this.context = context;
			context.addResourceLookup(new TestResourceLookup());
			context.addResourceInformationBuilder(new TestResourceInformationBuilder());

			context.addJacksonModule(new com.fasterxml.jackson.databind.module.SimpleModule() {

				private static final long serialVersionUID = 7829254359521781942L;

				@Override
				public String getModuleName() {
					return "test";
				}
			});

			context.addSecurityProvider(new SecurityProvider() {

				@Override
				public boolean isUserInRole(String role) {
					return "testRole".equals(role);
				}
			});

			context.addRepositoryDecoratorFactory(new TestRepositoryDecorator());
			context.addFilter(new TestFilter());
			context.addRepository(new ScheduleRepositoryImpl());
			context.addRepository(TestResource2.class, new TestRepository2());
			context.addRepository(TestResource2.class, TestResource2.class, new TestRelationshipRepository2());

			context.addExceptionMapper(new IllegalStateExceptionMapper());
			context.addExceptionMapperLookup(new ExceptionMapperLookup() {

				@Override
				public Set<JsonApiExceptionMapper> getExceptionMappers() {
					Set<JsonApiExceptionMapper> set = new HashSet<>();
					set.add(new SomeIllegalStateExceptionMapper());
					return set;
				}
			});
		}

		@Override
		public void init() {
			initialized = true;
		}
	}

	@JsonApiResource(type = "test2")
	static class TestResource2 {

		@JsonApiId
		private int id;

		private TestResource2 parent;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public TestResource2 getParent() {
			return parent;
		}

		public void setParent(TestResource2 parent) {
			this.parent = parent;
		}
	}

	class TestRelationshipRepository2 implements RelationshipRepositoryV2<TestResource2, Integer, TestResource2, Integer> {

		@Override
		public void setRelation(TestResource2 source, Integer targetId, String fieldName) {
		}

		@Override
		public void setRelations(TestResource2 source, Iterable<Integer> targetIds, String fieldName) {
		}

		@Override
		public void addRelations(TestResource2 source, Iterable<Integer> targetIds, String fieldName) {
		}

		@Override
		public void removeRelations(TestResource2 source, Iterable<Integer> targetIds, String fieldName) {
		}

		@Override
		public TestResource2 findOneTarget(Integer sourceId, String fieldName, QuerySpec queryParams) {
			return null;
		}

		@Override
		public ResourceList<TestResource2> findManyTargets(Integer sourceId, String fieldName, QuerySpec queryParams) {
			return null;
		}

		@Override
		public Class<TestResource2> getSourceResourceClass() {
			return TestResource2.class;
		}

		@Override
		public Class<TestResource2> getTargetResourceClass() {
			return TestResource2.class;
		}
	}

	class TestRepository2 implements ResourceRepositoryV2<TestResource2, Integer> {

		@Override
		public <S extends TestResource2> S save(S entity) {
			return null;
		}

		@Override
		public void delete(Integer id) {
		}

		@Override
		public Class<TestResource2> getResourceClass() {
			return TestResource2.class;
		}

		@Override
		public TestResource2 findOne(Integer id, QuerySpec querySpec) {
			return null;
		}

		@Override
		public ResourceList<TestResource2> findAll(QuerySpec querySpec) {
			return null;
		}

		@Override
		public ResourceList<TestResource2> findAll(Iterable<Integer> ids, QuerySpec querySpec) {
			return null;
		}

		@Override
		public <S extends TestResource2> S create(S entity) {
			return null;
		}
	}
}
