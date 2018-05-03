package org.bitbrawl.foodfight.field;

import com.google.gson.annotations.SerializedName;

/**
 * An event in the game that is worth some number of points. A team's
 * {@link Score} is based on the number of times that each of these events
 * occurs. Each event has a type, either {@link Type#ONE_TIME} or
 * {@link Type#ONGOING}. One-time events can happen a maximum of once per team
 * in a match, while ongoing events can occur multiple times. Each event has a
 * point value, and a negative point value represents a "bad" event that
 * competitors may wish to avoid.
 * <p>
 * Here is a list of all of the one-time events:
 * <ul>
 * <li>{@link #FIRST_MOVE}: 1 point
 * <li>{@link #FIRST_PICKUP}: 5 points
 * <li>{@link #FIRST_THROW}: 10 points
 * <li>{@link #FIRST_EAT}: 50 points
 * <li>{@link #FIRST_PLAYER_COLLISION}: -2 points
 * <li>{@link #FIRST_FOOD_COLLISION}: -100 points
 * </ul>
 * And here is a list of all of the ongoing events:
 * <ul>
 * <li>{@link #EVERY_EAT}: 5 points
 * <li>{@link #FOOD_ON_TABLE}: 1 point
 * <li>{@link #EVERY_PLAYER_COLLISION}: -1 point
 * <li>{@link #EVERY_FOOD_COLLISION}: -500 points
 * <li>{@link #TIE_BREAK}: 1 point
 * </ul>
 * 
 * @author Finn
 */
public enum Event {

	/** Occurs when a player performs a "move" action for the first time. */
	@SerializedName("firstMove")
	FIRST_MOVE("First move", Type.ONE_TIME, 1),
	/** Occurs when a player performs a "pickup" action for the first time. */
	@SerializedName("firstPickup")
	FIRST_PICKUP("First pickup", Type.ONE_TIME, 5),
	/** Occurs when a player performs a "throw" action for the first time. */
	@SerializedName("firstThrow")
	FIRST_THROW("First throw", Type.ONE_TIME, 10),
	/** Occurs when a player performs an "eat" action for the first time. */
	@SerializedName("firstEat")
	FIRST_EAT("First time eating", Type.ONE_TIME, 50),
	/** Occurs when a player collides with another for the first time. */
	@SerializedName("firstPlayerCollision")
	FIRST_PLAYER_COLLISION("First time hit by player", Type.ONE_TIME, -2),
	/** Occurs when a player collides with a food piece for the first time. */
	@SerializedName("firstFoodCollision")
	FIRST_FOOD_COLLISION("First time hit by food", Type.ONE_TIME, -100),
	/** Occurs every time a player performs an "eat" action. */
	@SerializedName("everyEat")
	EVERY_EAT("Every time eating", Type.ONGOING, 5),
	/** Occurs once per turn for every piece of food on a team's table. */
	@SerializedName("foodOnTable")
	FOOD_ON_TABLE("Food on table", Type.ONGOING, 1),
	/** Occurs every time a player collides with another. */
	@SerializedName("everyPlayerCollision")
	EVERY_PLAYER_COLLISION("Every time hit by player", Type.ONGOING, -1),
	/** Occurs every time a player collides with a food piece. */
	@SerializedName("everyFoodCollision")
	EVERY_FOOD_COLLISION("Every time hit by food", Type.ONGOING, -500),
	/** Occurs when a tie must be broken by distance to the center. */
	@SerializedName("tieBreak")
	TIE_BREAK("Tie break", Type.ONGOING, 1);

	private final String string;
	private final Type type;
	private final int pointValue;

	private Event(String string, Type type, int pointValue) {
		this.string = string;
		this.type = type;
		this.pointValue = pointValue;
	}

	/**
	 * Returns a human-readable name for this event that differs from what will
	 * be returned by {@link #name()}. It will be a bit more readable by human
	 * eyes, and is intended primarily for debugging purposes.
	 * 
	 * @return a human-readable name for this event
	 */
	@Override
	public String toString() {
		return string;
	}

	/**
	 * Returns the type of this action, either one-time or ongoing.
	 * 
	 * @return this action's type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Gets the value of this event to a team's score. A positive value
	 * indicates an increase to a team's score, while a negative value indicates
	 * a decrease.
	 * 
	 * @return this event's point value
	 */
	public int getPointValue() {
		return pointValue;
	}

	/**
	 * The type of an event, either one-time or ongoing. One-time events can
	 * only be scored once for a given team, while ongoing events may be scored
	 * multiple times.
	 * 
	 * @author Finn
	 */
	public enum Type {

		/** Indicates an event that can occur a maximum of once per match. */
		ONE_TIME("One-time"),
		/** Indicates an event that can occur multiple times per match. */
		ONGOING("Ongoing");

		private final String string;

		private Type(String string) {
			this.string = string;
		}

		/**
		 * Returns a human-readable name for this event type, either "One-time"
		 * or "Ongoing". This is primarily intended for debugging purposes.
		 * 
		 * @return a human-readable name for this event type
		 */
		@Override
		public String toString() {
			return string;
		}

	}

}
