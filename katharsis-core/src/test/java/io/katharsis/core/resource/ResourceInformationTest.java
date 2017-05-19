package io.katharsis.core.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Test;

import io.katharsis.core.engine.internal.information.resource.ResourceFieldImpl;
import io.katharsis.core.engine.information.resource.ResourceField;
import io.katharsis.core.engine.information.resource.ResourceFieldType;
import io.katharsis.core.engine.information.resource.ResourceInformation;
import io.katharsis.core.mock.models.Task;
import io.katharsis.core.engine.parser.TypeParser;

public class ResourceInformationTest {

	@Test
	public void onRelationshipFieldSearchShouldReturnExistingField() throws NoSuchFieldException {
		// GIVEN
		Field field = String.class.getDeclaredField("value");
		ResourceField idField = new ResourceFieldImpl("id", "id", ResourceFieldType.ID, field.getType(), field.getGenericType(), null);
		ResourceField resourceField = new ResourceFieldImpl("value", "value", ResourceFieldType.RELATIONSHIP, field.getType(), field.getGenericType(), "projects");
		TypeParser typeParser = new TypeParser();
		ResourceInformation sut = new ResourceInformation(typeParser, Task.class, "tasks", null, Arrays.asList(idField, resourceField));

		// WHEN
		ResourceField result = sut.findRelationshipFieldByName("value");

		// THEN
		assertThat(result.getUnderlyingName()).isEqualTo(field.getName());
	}
}
