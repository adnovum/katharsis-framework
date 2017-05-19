package io.katharsis.core.resource.paging;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import io.katharsis.core.resource.list.PagedResultList;

public class PagedResultListTest {

	@Test
	public void test() {
		PagedResultList<String> list = new PagedResultList<String>(new ArrayList<String>(), 13L);
		Assert.assertEquals(13L, list.getTotalCount().longValue());
	}
}
