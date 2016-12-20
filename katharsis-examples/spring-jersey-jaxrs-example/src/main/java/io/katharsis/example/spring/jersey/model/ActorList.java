package io.katharsis.example.spring.jersey.model;

import io.katharsis.resource.list.ResourceList;
import io.katharsis.resource.list.ResourceListBase;
import io.katharsis.response.paging.PagedLinksInformation;
import io.katharsis.response.paging.PagedMetaInformation;

public class ActorList extends ResourceListBase<Actor, PagedMetaInformation, PagedLinksInformation> {

    public ActorList(ResourceList<Actor> baseList) {
        super();
        this.setWrappedList(baseList);
        this.setLinks(baseList.getLinks());
        this.setMeta(baseList.getMeta());
    }

    public ActorList(){}

}
