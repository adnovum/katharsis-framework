package io.katharsis.example.spring.jersey.model;

import io.katharsis.response.LinksInformation;
import io.katharsis.response.paging.PagedLinksInformation;

public class MovieListLinks implements LinksInformation, PagedLinksInformation {

	private String first;
	private String last;
	private String next;
	private String prev;

	@Override
	public String getFirst() {
		return first;
	}

	@Override
	public void setFirst(String first) {
		this.first = first;
	}

	@Override
	public String getLast() {
		return last;
	}

	@Override
	public void setLast(String last) {
		this.last = last;
	}

	@Override
	public String getNext() {
		return next;
	}

	@Override
	public void setNext(String next) {
		this.next = next;
	}

	@Override
	public String getPrev() {
		return prev;
	}

	@Override
	public void setPrev(String prev) {
		this.prev = prev;
	}
}