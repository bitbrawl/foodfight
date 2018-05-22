package org.bitbrawl.foodfight.util;

import java.util.Objects;

import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Inventory;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Player.Hand;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.Immutable;

/**
 * A utility class to get information about players on the field.
 * 
 * @author Finn
 */
@Immutable
public final class PlayerUtils {

	private PlayerUtils() {
		throw new AssertionError("PlayerUtils is not instantiable");
	}

	/**
	 * Determines whether the given action is currently valid for the given
	 * player.
	 * <p>
	 * There is one special case in which the result of this method can be
	 * misleading. If two players attempt to pick up the same food on the same
	 * turn, turn order is randomized, so one of them will be able to grab it
	 * and the other won't. Thus, if a controller returns one of the "pickup"
	 * actions because this method returns true, it may actually end up being an
	 * invalid action if another player picks up the food.
	 * 
	 * @param field
	 *            the field that the player is playing on
	 * @param player
	 *            the player on the field potentially performing the action
	 * @param action
	 *            the action in question
	 * @return true if the player can play the action, otherwise false
	 * @throws NullPointerException
	 *             if field or player is null
	 */
	public static boolean isValidAction(Field field, Player player, Action action) {
		Objects.requireNonNull(field, "field cannot be null");
		Objects.requireNonNull(player, "player cannot be null");
		if (action == null)
			return true;
		if (action.isTurning() || action.equals(Action.DUCK))
			return true;

		Vector location = player.getLocation();
		double x = location.getX(), y = location.getY();
		Direction heading = player.getHeading();
		Inventory inventory = player.getInventory();
		double halfPi = 0.5 * Math.PI;

		switch (action) {
		case MOVE_FORWARD:
			if (x <= Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.WEST)) < halfPi)
					return false;
			} else if (x >= Field.WIDTH - Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.EAST)) < halfPi)
					return false;
			}
			if (y <= Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.SOUTH)) < halfPi)
					return false;
			} else if (y >= Field.DEPTH - Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.NORTH)) < halfPi)
					return false;
			}
			if (isAgainstAnyTable(field, player, true))
				return false;
			return true;
		case MOVE_BACKWARD:
			if (x <= Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.EAST)) < halfPi)
					return false;
			} else if (x >= Field.WIDTH - Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.WEST)) < halfPi)
					return false;
			}
			if (y <= Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.NORTH)) < halfPi)
					return false;
			} else if (y >= Field.DEPTH - Player.COLLISION_RADIUS) {
				if (Math.abs(Direction.difference(heading, Direction.SOUTH)) < halfPi)
					return false;
			}
			if (isAgainstAnyTable(field, player, false))
				return false;
			return true;
		case PICKUP_LEFT:
			if (inventory.get(Hand.LEFT) != null)
				return false;
			for (Food food : field.getFood())
				if (canPickup(player, food, Hand.LEFT))
					return true;
			return canPickupFromAnyTable(field, player);
		case PICKUP_RIGHT:
			if (inventory.get(Hand.RIGHT) != null)
				return false;
			for (Food food : field.getFood())
				if (canPickup(player, food, Hand.RIGHT))
					return true;
			return canPickupFromAnyTable(field, player);
		case THROW_LEFT:
		case EAT_LEFT:
			if (inventory.get(Hand.LEFT) == null)
				return false;
			return true;
		case THROW_RIGHT:
		case EAT_RIGHT:
			if (inventory.get(Hand.RIGHT) == null)
				return false;
			return true;
		default:
			throw new AssertionError();
		}

	}

	/**
	 * Determines whether the given player can pick up the given food piece,
	 * with the given hand. This is true if the food piece is on the ground and
	 * "in range" of the player. A player's range is defined by a sector,
	 * centered on the player's center, with a radius of
	 * {@link Player#REACH_DISTANCE} and an angle measurement of
	 * {@link Player#REACH_RANGE}, centered on the direction returned by
	 * {@link #getArmDirection(Player, Hand)}.
	 * 
	 * @param player
	 *            the player attempting a "pickup" action
	 * @param food
	 *            the food that the player may pick up
	 * @param hand
	 *            the hand with which the player is attempting the pickup
	 * @return true if the player can pick up the food, otherwise false
	 * @throws NullPointerException
	 *             if player, food, or hand is null
	 */
	public static boolean canPickup(Player player, Food food, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(food, "food cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		if (food.getHeight() > 0.0)
			return false;

		Vector playerLocation = player.getLocation();
		Vector foodLocation = food.getLocation();

		Vector translation = foodLocation.subtract(playerLocation);
		if (translation.getMagnitude() > Player.REACH_DISTANCE)
			return false;

		Direction toFood = translation.getDirection();
		return Math.abs(Direction.difference(getArmDirection(player, hand), toFood)) < Player.REACH_RANGE / 2;

	}

	/**
	 * Determines whether the given player is against the table. The
	 * movingForward parameter refers to the player's direction of travel. If
	 * movingForward is true, then the player must be facing toward the table
	 * for this method to return true. If movingForward is false, then the
	 * player must be facing away from the table for this method to return true.
	 * In other words, if movingForward is true, this method returns true if and
	 * only if the table would prevent the player from performing an
	 * {@link Action#MOVE_FORWARD}, and if movingForward is false, this method
	 * returns true if and only if the table would prevent the player from
	 * performing and {@link Action#MOVE_BACKWARD}.
	 * 
	 * @param player
	 *            the player in question
	 * @param table
	 *            the table that the player may be against
	 * @param movingForward
	 *            true if the player is moving forward, false if backward
	 * @return true if the player would be impeded by the table, otherwise false
	 * @throws NullPointerException
	 *             if player or table is null
	 */
	public static boolean isAgainstTable(Player player, Table table, boolean movingForward) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(table, "table cannot be null");

		Vector location = player.getLocation();
		double playerX = location.getX();
		double playerY = location.getY();
		Direction heading = player.getHeading();
		double halfPi = Math.PI / 2.0;

		boolean movingNorth, movingSouth, movingEast, movingWest;
		if (movingForward) {
			movingNorth = Math.abs(Direction.difference(heading, Direction.NORTH)) < halfPi;
			movingSouth = Math.abs(Direction.difference(heading, Direction.SOUTH)) < halfPi;
			movingEast = Math.abs(Direction.difference(heading, Direction.EAST)) < halfPi;
			movingWest = Math.abs(Direction.difference(heading, Direction.WEST)) < halfPi;
		} else {
			movingNorth = Math.abs(Direction.difference(heading, Direction.NORTH)) > halfPi;
			movingSouth = Math.abs(Direction.difference(heading, Direction.SOUTH)) > halfPi;
			movingEast = Math.abs(Direction.difference(heading, Direction.EAST)) > halfPi;
			movingWest = Math.abs(Direction.difference(heading, Direction.WEST)) > halfPi;
		}

		Vector tableLocation = table.getLocation();
		double tableX = tableLocation.getX();
		double tableY = tableLocation.getY();

		double tableRadiusExtended = Table.RADIUS + Player.COLLISION_RADIUS;
		double northEdge = tableY + tableRadiusExtended;
		double southEdge = tableY - tableRadiusExtended;
		double eastEdge = tableX + tableRadiusExtended;
		double westEdge = tableX - tableRadiusExtended;

		if (westEdge < playerX && playerX < eastEdge) {

			if (movingNorth && southEdge <= playerY && playerY < tableY)
				return true;
			if (movingSouth && tableY < playerY && playerY <= northEdge)
				return true;

		}

		if (southEdge < playerY && playerY < northEdge) {

			if (movingEast && westEdge <= playerX && playerX < tableX)
				return true;
			if (movingWest && tableX < playerX && playerX <= eastEdge)
				return true;

		}

		return false;

	}

	/**
	 * Gets the direction of a player's arm. Equivalently, this is the player's
	 * heading, added to either {@link Player#LEFT_ARM_DIRECTION} or
	 * {@link Player#RIGHT_ARM_DIRECTION}. The arm direction of a player is used
	 * to determine where they can pickup food (see
	 * {@link #canPickup(Player, Food, Hand)}) and where thrown food is spawened
	 * (see {@link #getArmLocation(Player, Hand)}).
	 * 
	 * @param player
	 *            the player in question
	 * @param hand
	 *            the side of the player to check
	 * @return the direction of that player's arm
	 * @throws NullPointerException
	 *             if player or hand is null
	 */
	public static final Direction getArmDirection(Player player, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		switch (hand) {
		case LEFT:
			return player.getHeading().add(Player.LEFT_ARM_DIRECTION);
		case RIGHT:
			return player.getHeading().add(Player.RIGHT_ARM_DIRECTION);
		default:
			throw new AssertionError();
		}

	}

	/**
	 * Gets the location of the player's arm. This is the location that is
	 * {@link Player#REACH_DISTANCE} away from the player's center, in the
	 * direction of {@link #getArmDirection(Player, Hand)}. Equivalently, this
	 * is the location at which thrown food is spawned.
	 * 
	 * @param player
	 *            the player whose arm to check
	 * @param hand
	 *            the side of the player to check
	 * @return the location at which thrown food would be spawned
	 * @throws NullPointerException
	 *             if player or hand is null
	 */
	public static final Vector getArmLocation(Player player, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		return player.getLocation().add(Vector.polar(Player.REACH_DISTANCE, getArmDirection(player, hand)));

	}

	/**
	 * Gets the multiplier for a player's movement, based on their energy.
	 * Whenever a player moves, the distance traveled will be multiplied by the
	 * result of this function. The answer is equivalent to e<sup>energy /
	 * {@link Player#MAX_ENERGY} - 1</sup>
	 * 
	 * @param energy
	 *            the player's energy
	 * @return the factor used to calculate the player's travel distance.
	 */
	public static double getMoveMultiplier(double energy) {
		if (0.0 <= energy && energy <= Player.MAX_ENERGY)
			return Math.exp(energy / Player.MAX_ENERGY - 1.0);
		throw new IllegalArgumentException("energy must be within [0, 100], but is: " + energy);
	}

	private static boolean isAgainstAnyTable(Field field, Player player, boolean movingForward) {
		for (Team team : field.getTeams())
			if (isAgainstTable(player, team.getTable(), movingForward))
				return true;
		return false;
	}

	private static boolean canPickupFromAnyTable(Field field, Player player) {
		for (Team team : field.getTeams()) {
			Table table = team.getTable();
			if (isAgainstTable(player, table, true) && !table.getFood().isEmpty())
				return true;
		}
		return false;
	}

}
