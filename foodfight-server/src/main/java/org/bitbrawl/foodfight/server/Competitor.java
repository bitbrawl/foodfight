package org.bitbrawl.foodfight.server;

public final class Competitor {

	private final int id;
	private final String username;

	public Competitor(int id, String username) {
		this.id = id;
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

}
