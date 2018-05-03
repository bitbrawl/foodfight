package org.bitbrawl.foodfight.field;

import java.util.Set;

/**
 * The entire playing field for a match. A Field object can be used to get all
 * of the information about the current state of the game.
 * <p>
 * A field has three dimensions: width, depth, and height. In the generated
 * match videos, the perspective is from above, so {@link #WIDTH} and
 * {@link #DEPTH} refer to the width and height of the video. But do not confuse
 * depth with height; the y-coordinate returned by {@link Player#getLocation()}
 * represents an entirely different dimension than the y-coordinate returned by
 * {@link Player#getHeight()}.
 * <p>
 * 
 * @author Finn
 */
public interface Field {

	/**
	 * Returns the number of the current turn. Note that turns are 1-indexed, so
	 * the first action that players will play will be on turn 1. The final turn
	 * will be the number {@link #TOTAL_TURNS}.
	 * 
	 * @return the current turn being played on this field
	 */
	public int getTurnNumber();

	/**
	 * Returns the type of match being played on this field. This will not
	 * change between turns.
	 * 
	 * @return the type of match being played on this field.
	 */
	public MatchType getMatchType();

	/**
	 * Returns a set containing all of the teams on the field. The teams on the
	 * field will not change between turns (though the properties of the teams
	 * may). Iteration over this set will always produce the same teams in the
	 * same order.
	 * 
	 * @return all of the teams playing on the field
	 */
	public Set<Team> getTeams();

	/**
	 * Returns the team on this field with the given symbol. That is,
	 * {@link Team#getSymbol()} will return symbol for the returned team. If
	 * there is no team playing on this field that has the given symbol, this
	 * method returns null. The functionality of this method could be achieved
	 * by iterating over the set returned by {@link #getTeams()}, but this
	 * method exists for convenience.
	 * 
	 * @param symbol
	 *            the identifying symbol of the desired team
	 * @return the team with the desired symbol, or null if none exists
	 */
	public Team getTeam(char symbol);

	/**
	 * Returns the team for which the given player is playing. For the returned
	 * team, {@link Team#getPlayers()} will return a set that contains the given
	 * player. If no such team exists (meaning that the player is not currently
	 * on the field), then this method returns null. The functionality of this
	 * method could be achieved by iterating over the set returned by
	 * {@link #getTeams()}, but this method exists for convenience.
	 * 
	 * @param player
	 *            a player on the desired team
	 * @return the team on which the player is playing, or null if none exists
	 */
	public Team getTeam(Player player);

	/**
	 * Returns a set containing all players on the field. The functionality of
	 * this method could be achieved by iterating over the set returned by
	 * {@link #getTeams()}, and the set returned by {@link Team#getPlayers()}
	 * for each team, but this method exists for convenience.
	 * 
	 * @return all of the players playing on this field
	 */
	public Set<Player> getPlayers();

	/**
	 * Returns the player on this field with the given symbol. That is,
	 * {@link Player#getSymbol()} will return symbol for the returned player. If
	 * there is no player playing on this field that has the given symbol, this
	 * method returns null. The functionality of this method could be achieved
	 * by iterating over the set returned by {@link #getTeams()}, but this
	 * method exists for convenience.
	 * 
	 * @param symbol
	 *            the identifying symbol of the desired player
	 * @return the player with the desired symbol, or null if none exists
	 */
	public Player getPlayer(char symbol);

	/**
	 * Returns a set containing all of the food on the field. Note that this set
	 * will not contain food that is on a {@link Table} or in a player's
	 * {@link Inventory}; it includes only food that is on the ground or in the
	 * air.
	 * <p>
	 * There are three ways for the size of this set to decrease: if a player
	 * picks up a food piece (moving the food into the player's inventory), if a
	 * player is struck by a flying food piece (despawning the food piece), or
	 * if a food piece passes over a table (moving the food onto the table).
	 * <p>
	 * There are also two ways for the size of this set to increase: if a player
	 * throws a food piece (creating a new flying food piece), or if a new food
	 * piece is spawned (creating a new food piece on the ground). A new food
	 * piece is spawned on a turn if the total number of types of food on the
	 * field (including tables an inventories) is less than {@link #MAX_FOOD},
	 * and then only with probability {@link Food#RESPAWN_RATE}. Food pieces are
	 * spawned at a random, non-colliding position on the field.
	 * 
	 * @return all of the food on the ground or in the air on this field
	 */
	public Set<Food> getFood();

	/**
	 * Returns the food piece on this field with the given type. That is,
	 * {@link Food#getType()} will return type for the returned food piece. If
	 * there is no food piece on this field that has the given type, this method
	 * returns null. There will never be two food pieces of the same type on the
	 * field at a time, so this method returns the unique food piece with the
	 * desired type. Note that food on a {@link Table} or in a player's
	 * {@link Inventory} is ignored, so if food of this type is on a table or in
	 * a player's inventory, this method returns null. The functionality of this
	 * method could be achieved by iterating over the set returned by
	 * {@link #getFood()}, but this method exists for convenience.
	 * 
	 * @param type
	 *            the type of the desired food piece
	 * @return the food piece with the desired type, or null if none exists
	 */
	public Food getFood(Food.Type type);

	/**
	 * Returns a set of all of the collisions that occurred on the field on the
	 * previous turn. This set will be empty if no collisions occurred on the
	 * previous turn. Note that this set will never contain the same collision
	 * objects on two different turns.
	 * 
	 * @return all of the collisions from the previous turn
	 */
	public Set<Collision> getCollisions();

	/** The depth, or maximum y-coordinate, of the field. */
	public static final double DEPTH = 1080.0;
	/** The width, or maximum x-coordinate, of the field. */
	public static final double WIDTH = 1440.0;
	/** The maximum food pieces that can exist on the field at a time. */
	public static final int MAX_FOOD = 4;
	/** The total number of turns in a match. */
	public static final int TOTAL_TURNS = 2000;

}
