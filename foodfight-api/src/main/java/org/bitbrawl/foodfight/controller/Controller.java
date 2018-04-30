package org.bitbrawl.foodfight.controller;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.Immutable;

/**
 * The code that is controlling the actions of a player. A competitor will not
 * typically need this class specifically except for its {@link Action} enum. To
 * control a player on the field, a competitor needs to override
 * {@link JavaController}, a class that implements this interface.
 * 
 * @author Finn
 */
public interface Controller {

	/**
	 * Plays a single turn of the game, given a field, team, and player. The
	 * player and team refer specifically to the player the controller is
	 * controlling and the team of that player, respectively. If this method
	 * returns null, that indicates that the controller does not wish to play
	 * any action on this turn.
	 * 
	 * @param field
	 *            the field on which the player is playing
	 * @param team
	 *            the team on which the player is playing
	 * @param player
	 *            the player that the controller's actions control
	 * @return the action desired by the controller
	 */
	public Action playAction(Field field, Team team, Player player);

	/**
	 * An action that a controller can choose to play on a turn. Each turn, each
	 * controller can select one of these actions based on the current field
	 * state.
	 * 
	 * @author Finn
	 */
	@Immutable
	public enum Action {

		/** A movement in the direction that a player is facing. */
		MOVE_FORWARD("Move forward"),
		/** A movement in the backward direction. */
		MOVE_BACKWARD("Move backward"),
		/** A turn in the counter-clockwise direction. */
		TURN_LEFT("Turn left"),
		/** A turn in the clockwise direction. */
		TURN_RIGHT("Turn right"),
		/** A duck, which will decrease a player's height. */
		DUCK("Duck"),
		/** A pickup of a piece of food into the player's left hand. */
		PICKUP_LEFT("Pick up with left hand"),
		/** A pickup of a piece of food into the player's right hand. */
		PICKUP_RIGHT("Pick up with right hand"),
		/** A throw from the player's left hand. */
		THROW_LEFT("Throw from left hand"),
		/** A throw from the player's right hand. */
		THROW_RIGHT("Throw from right hand"),
		/** The action of eating the food in the player's left hand. */
		EAT_LEFT("Eat from left hand"),
		/** The action of eating the food in the player's right hand. */
		EAT_RIGHT("Eat from right hand");

		private final String string;

		private Action(String name) {
			this.string = name;
		}

		/**
		 * Returns a human-readable name for this action that differs from what
		 * will be returned by {@link #name()}. It will be a bit more readable
		 * by human eyes, and is intended primarily for debugging purposes.
		 * 
		 * @return a human-readable name for this action
		 */
		@Override
		public String toString() {
			return string;
		}

		/**
		 * Determines whether this action is one of the "move" actions. The two
		 * move actions are {@link #MOVE_FORWARD} and {@link #MOVE_BACKWARD}.
		 * 
		 * @return true if this action moves the player, otherwise false
		 */
		public boolean isMoving() {
			switch (this) {
			case MOVE_FORWARD:
			case MOVE_BACKWARD:
				return true;
			default:
				return false;
			}
		}

		/**
		 * Determines whether this action is one of the "turn" actions. The two
		 * turn actions are {@link #TURN_LEFT} and {@link #TURN_RIGHT}.
		 * 
		 * @return true if this action turns the player, otherwise false
		 */
		public boolean isTurning() {
			switch (this) {
			case TURN_LEFT:
			case TURN_RIGHT:
				return true;
			default:
				return false;
			}
		}

		/**
		 * Determines whether this action is one of the "pickup" actions. The
		 * two pickup actions are {@link #PICKUP_LEFT} and
		 * {@link #PICKUP_RIGHT}.
		 * 
		 * @return true if this action picks up food, otherwise false
		 */
		public boolean isPickingUp() {
			switch (this) {
			case PICKUP_LEFT:
			case PICKUP_RIGHT:
				return true;
			default:
				return false;
			}
		}

		/**
		 * Determines whether this action is one of the "throw" actions. The two
		 * throw actions are {@link #THROW_LEFT} and {@link #THROW_RIGHT}.
		 * 
		 * @return true if this action throws food, otherwise false
		 */
		public boolean isThrowing() {
			switch (this) {
			case THROW_LEFT:
			case THROW_RIGHT:
				return true;
			default:
				return false;
			}
		}

		/**
		 * Determines whether this action is one of the "eat" actions. The two
		 * eat actions are {@link #EAT_LEFT} and {@link #EAT_RIGHT}.
		 * 
		 * @return true if this action eats food, otherwise false
		 */
		public boolean isEating() {
			switch (this) {
			case EAT_LEFT:
			case EAT_RIGHT:
				return true;
			default:
				return false;
			}
		}

	}

}
