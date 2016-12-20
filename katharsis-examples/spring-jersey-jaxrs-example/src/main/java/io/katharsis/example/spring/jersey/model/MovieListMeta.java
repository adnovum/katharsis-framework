package io.katharsis.example.spring.jersey.model;

import io.katharsis.response.MetaInformation;
import io.katharsis.response.paging.PagedMetaInformation;

public class MovieListMeta implements MetaInformation, PagedMetaInformation {

	@Override
	public Long getTotalResourceCount() {
		return null;
	}

	@Override
	public void setTotalResourceCount(Long totalResourceCount) {

	}
}
