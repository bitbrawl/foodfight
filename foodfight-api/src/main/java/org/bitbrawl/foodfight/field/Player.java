package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.PlayerUtils;
import org.bitbrawl.foodfight.util.RandomScalar;

import com.google.gson.annotations.SerializedName;

import net.jcip.annotations.Immutable;

/**
 * A single player on the field. Each player is controlled by a separate
 * {@link Controller}.
 * <p>
 * In terms of collisions, players can be thought of as cylinders on the field.
 * The bottom plane is at a height of zero, corresponding with the ground plane
 * of the field. The top plane is at a height of {@link #getHeight()}, which is
 * the top of the player's head. The radius of the cylinder (for all players) is
 * {@link #COLLISION_RADIUS}. The central axis of the cylinder can be located
 * with {@link #getLocation()}, relative to the bottom-left corner of the field
 * when viewed from above.
 * 
 * @author Finn
 * @see Controller
 * @see PlayerUtils
 * @see Team
 */
public interface Player extends Locatable {

	/**
	 * Gets the character that uniquely identifies this player in the game. This
	 * character will always be a digit, and it will never change for a given
	 * player.
	 * 
	 * @return the character uniquely identifying this player
	 */
	public char getSymbol();

	/**
	 * Gets the height of the top of this player's head. This value will always
	 * be somewhere between {@link #MIN_HEIGHT} and {@link #HEIGHT}. The only
	 * way for this value to decrease is for a player to duck. If a piece of
	 * food has a higher height than a given player, it will not collide with
	 * that player.
	 * 
	 * @return this player's height
	 */
	public double getHeight();

	/**
	 * Gets the direction that this player is currently facing. The only way for
	 * this value to change is if this player actively turns, meaning its
	 * controller returns either {@link Action#TURN_LEFT} or
	 * {@link Action#TURN_RIGHT}. If a player moves forward, they will move in
	 * the direction of their heading.
	 * 
	 * @return the direction toward which this player is currently pointing
	 */
	public Direction getHeading();

	/**
	 * Gets this player's inventory. This method can be used to determine what a
	 * player is currently holding.
	 * 
	 * @return the player's inventory
	 */
	public Inventory getInventory();

	/**
	 * Gets this player's amount of energy. This value will always be between
	 * 0.0 and {@link #MAX_ENERGY}, inclusive. A player's energy affects how
	 * quickly they are able to move. Each turn, a small amount of energy
	 * ({@link #ENERGY_DECREMENT}) is subtracted from the energy total of every
	 * player who isn't idling, ducking, or eating, so players who move more
	 * early in the game may move more slowly later in the game. Additionally,
	 * colliding with a piece of food or another player will cost a player
	 * energy. The only way for a player's energy to increase is if they eat a
	 * piece of food.
	 * 
	 * @return the player's total energy
	 */
	public double getEnergy();

	/** The normal (non-ducking) height of every player. */
	public static final double HEIGHT = 200.0;
	/** The minimum (ducking) height of every player. */
	public static final double MIN_HEIGHT = 100.0;
	/** The height at which thrown food starts. */
	public static final RandomScalar THROW_HEIGHT = new RandomScalar(150.0, 10.0);
	/** The distance a player can move forward on a turn. */
	public static final RandomScalar FORWARD_MOVEMENT_SPEED = new RandomScalar(10.0, 1.0);
	/** The distance a player can move backward on a turn. */
	public static final RandomScalar REVERSE_MOVEMENT_SPEED = new RandomScalar(5.0, 1.0);
	/** The per-turn decrease in height of a ducking player. */
	public static final RandomScalar DUCK_SPEED = new RandomScalar(5.0, 1.0);
	/** The per-turn change in rotation (in radians) of a turning player. */
	public static final RandomScalar TURN_SPEED = new RandomScalar(0.05, 0.01);
	/** The maximum distance for a piece of food that can be picked up. */
	public static final double REACH_DISTANCE = 100.0;
	/** The rotational range for a piece of food that can be picked up. */
	public static final double REACH_RANGE = Math.PI / 3.0;
	/** The direction of a player's left arm relative to their heading. */
	public static final double LEFT_ARM_DIRECTION = -Math.PI / 6.0;
	/** The direction of a player's right arm relative to their heading. */
	public static final double RIGHT_ARM_DIRECTION = Math.PI / 6.0;
	/** The radius for a player's collisions with other field objects. */
	public static final double COLLISION_RADIUS = 50.0;
	/** The maximum amount of energy that a player can have. */
	public static final double MAX_ENERGY = 100.0;
	/** The small amount of energy that is decremented from active players. */
	public static final double ENERGY_DECREMENT = 0.02;
	/** The amount of damage done by player-player collisions. */
	public static final RandomScalar COLLISION_DAMAGE = new RandomScalar(2.0, 1.0);

	/**
	 * A player's hand, either left or right. This enum is used in methods that
	 * need to refer to a specific side of a player, for example in the
	 * {@link Inventory} methods.
	 * 
	 * @author Finn
	 */
	@Immutable
	public enum Hand {

		/** A player's left hand. */
		@SerializedName("left")
		LEFT("Left hand"),
		/** A player's right hand. */
		@SerializedName("right")
		RIGHT("Right hand");

		private final String name;

		private Hand(String name) {
			this.name = name;
		}

		/**
		 * Returns a human-readable name for this hand, either "Left hand" or
		 * "Right hand". This is primarily intended for debugging purposes.
		 * 
		 * @return a human-readable name for this hand
		 */
		@Override
		public String toString() {
			return name;
		}

	}

}
