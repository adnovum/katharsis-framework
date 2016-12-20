package io.katharsis.example.spring.jersey.model;

import java.util.List;

import io.katharsis.resource.annotations.JsonApiId;
import io.katharsis.resource.annotations.JsonApiLinksInformation;
import io.katharsis.resource.annotations.JsonApiMetaInformation;
import io.katharsis.resource.annotations.JsonApiResource;
import io.katharsis.resource.annotations.JsonApiToMany;

@JsonApiResource(type = "movie")
public class Movie {

	@JsonApiId
	private String id;
	private String title;
	private int year;
	@JsonApiToMany // uses lazy loading
	private List<Actor> actors;

	@JsonApiMetaInformation
	private MovieMeta meta;

	@JsonApiLinksInformation
	private MovieLinks links;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Actor> getActors() {
		return actors;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	public MovieMeta getMeta() {
		return meta;
	}

	public void setMeta(MovieMeta meta) {
		this.meta = meta;
	}

	public MovieLinks getLinks() {
		return links;
	}

	public void setLinks(MovieLinks links) {
		this.links = links;
	}
}
