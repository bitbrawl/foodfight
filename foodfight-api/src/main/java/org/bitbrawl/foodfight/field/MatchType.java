package org.bitbrawl.foodfight.field;

/**
 * A type of match. There are three types of matches, and each match is of one
 * of the three types.
 * 
 * @author Finn
 */
public enum MatchType {
	/** A 1v1 duel between two players. */
	DUEL("Duel", 2, 2),
	/** A 1v1v1 free-for-all between three players. */
	FREE_FOR_ALL("Free-for-all", 3, 3),
	/** A 2v2 team match for four players. */
	TEAM("Team", 2, 4);

	private final String string;
	private final int numberOfTeams;
	private final int numberOfPlayers;

	private MatchType(String string, int numberOfTeams, int numberOfPlayers) {
		this.string = string;
		this.numberOfTeams = numberOfTeams;
		this.numberOfPlayers = numberOfPlayers;
	}

	/**
	 * Returns a human-readable name for this match type, either "Duel",
	 * "Free-for-all", or "Team". This is primarily intended for debugging
	 * purposes.
	 * 
	 * @return a human-readable name for this match type
	 */
	@Override
	public String toString() {
		return string;
	}

	/**
	 * Returns the number of teams in matches of this type. For duels and team
	 * matches, this will be 2, and for free-for-alls, this will be 3.
	 * 
	 * @return the number of teams in this match type
	 */
	public int getNumberOfTeams() {
		return numberOfTeams;
	}

	/**
	 * Returns the total number of players in matches of this type. For duels,
	 * this will be 2, for free-for-alls, this will be 3, and for team matches,
	 * this will be 4.
	 * 
	 * @return the total number of players in this match type
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * Returns the match type that has the given number of players. In other
	 * words, {@link #getNumberOfPlayers()} will return numPlayers for the
	 * returned match type.
	 * 
	 * @param numPlayers
	 *            the number of players in the desired match type
	 * @return the match type with the desired number of players
	 * @throws IllegalArgumentException
	 *             if numPlayers is not 2, 3, or 4
	 */
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
