package org.bitbrawl.foodfight.server;

public final class CompetitorPair {

	private final int firstId, secondId;

	public CompetitorPair(int firstId, int secondId) {
		this.firstId = firstId;
		this.secondId = secondId;
	}

	public int getFirstId() {
		return firstId;
	}

	public int getSecondId() {
		return secondId;
	}

}
