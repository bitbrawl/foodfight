package org.bitbrawl.foodfight.field;

import java.util.Set;

/**
 * A team of players, and their associated information. Note that in matches of
 * type {@link MatchType#DUEL} and {@link MatchType#FREE_FOR_ALL}, each team
 * will contain a single player.
 * 
 * @author Finn
 * @see Field
 */
public interface Team {

	/**
	 * Gets the symbol that uniquely identifies this team. This will always be a
	 * capital letter, and will not change over the course of a match.
	 * 
	 * @return the character that uniquely identifies this team
	 */
	public char getSymbol();

	/**
	 * Gets the set of all players on this team. This set is unmodifiable, and
	 * will not change over the course of a match.
	 * 
	 * @return the set of all players who are playing on this team
	 */
	public Set<Player> getPlayers();

	/**
	 * Gets the player on this team with the given identifying symbol. That is,
	 * for the player returned, {@link Player#getSymbol()} will return the
	 * symbol passed into this method. If no such player exists, null is
	 * returned.
	 * 
	 * @param symbol
	 *            the desired identifying symbol
	 * @return the player identified by that symbol
	 */
	public Player getPlayer(char symbol);

	/**
	 * Gets the table that scores points for this team. Any food on this table
	 * will earn points for the corresponding team.
	 * 
	 * @return this team's table
	 */
	public Table getTable();

	/**
	 * Gets this team's score. To determine which points-earning events occurred
	 * on a turn, a controller can check for changes to a team's score. Note
	 * that the score does not keep track of which player on the team earned the
	 * points, only that someone on the team did.
	 * 
	 * @return
	 */
	public Score getScore();

}
