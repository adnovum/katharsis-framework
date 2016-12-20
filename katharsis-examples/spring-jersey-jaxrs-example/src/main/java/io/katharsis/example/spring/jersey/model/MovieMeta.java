package io.katharsis.example.spring.jersey.model;

public class MovieMeta  {

	public MovieMeta(int averageStars) {
		this.averageStars = averageStars;
	}

	public MovieMeta() {
	}

	private int averageStars;

	public int getAverageStars() {
		return averageStars;
	}

	public void setAverageStars(int averageStars) {
		this.averageStars = averageStars;
	}
}
