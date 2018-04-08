package org.bitbrawl.foodfight.server;

public final class ControllerVersion {

	private final int competitorId;
	private final String name;

	public ControllerVersion(int competitorId, String name) {
		this.competitorId = competitorId;
		this.name = name;
	}

	public int getCompetitorId() {
		return competitorId;
	}

	public String getName() {
		return name;
	}

}
