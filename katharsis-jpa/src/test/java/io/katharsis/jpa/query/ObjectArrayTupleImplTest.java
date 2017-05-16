package io.katharsis.jpa.query;

import javax.persistence.TupleElement;

import com.querydsl.core.types.Expression;
import io.katharsis.jpa.internal.query.backend.querydsl.QuerydslObjectArrayTupleImpl;
import org.junit.Assert;
import org.junit.Test;

public class ObjectArrayTupleImplTest {

	private QuerydslObjectArrayTupleImpl impl = new QuerydslObjectArrayTupleImpl(new Object[] { "0", "1" }, null);

	@Test(expected = UnsupportedOperationException.class)
	public void testGetByExpressionNotSupported() {
		impl.get((Expression<?>) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetByTupleNotSupported() {
		impl.get((TupleElement<?>) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetByNameNotSupported() {
		impl.get((String) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetElementsNotSupported() {
		impl.getElements();
	}

	@Test
	public void testReduce() {
		Assert.assertEquals(2, impl.size());
		Assert.assertEquals(2, impl.size());
		Assert.assertArrayEquals(new Object[] { "0", "1" }, impl.toArray());
		impl.reduce(1);
		Assert.assertEquals("1", impl.get(0, String.class));
		Assert.assertEquals(1, impl.size());
		Assert.assertArrayEquals(new Object[] { "1" }, impl.toArray());
	}
}
