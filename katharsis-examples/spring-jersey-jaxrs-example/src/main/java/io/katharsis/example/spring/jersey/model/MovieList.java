package io.katharsis.example.spring.jersey.model;

import io.katharsis.resource.list.ResourceList;
import io.katharsis.resource.list.ResourceListBase;

public class MovieList extends ResourceListBase<Movie, MovieListMeta, MovieListLinks> {
    public MovieList(ResourceList<Movie> baseList) {
        super();
        this.setWrappedList(baseList);
        this.setLinks(baseList.getLinks());
        this.setMeta(baseList.getMeta());
    }

    public MovieList() {
    }
}
