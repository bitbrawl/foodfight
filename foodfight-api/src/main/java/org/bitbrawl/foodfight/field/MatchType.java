package org.bitbrawl.foodfight.field;

public enum MatchType {
	DUEL("duel", 2, 2),
	FREE_FOR_ALL("free-for-all", 3, 3),
	TEAM("team", 2, 4);

	private final String name;
	private final int numberOfTeams;
	private final int numberOfPlayers;

	private MatchType(String name, int numberOfTeams, int numberOfPlayers) {
		this.name = name;
		this.numberOfTeams = numberOfTeams;
		this.numberOfPlayers = numberOfPlayers;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getNumberOfTeams() {
		return numberOfTeams;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public static MatchType byNumberOfPlayers(int numPlayers) {
		switch (numPlayers) {
		case 2:
			return DUEL;
		case 3:
			return FREE_FOR_ALL;
		case 4:
			return TEAM;
		default:
			throw new IllegalArgumentException(Integer.toString(numPlayers));
		}
	}

}
