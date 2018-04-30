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

@Immutable
public final class PlayerUtils {

	private PlayerUtils() {
		throw new AssertionError("PlayerUtils is not instantiable");
	}

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
			if (x <= Player.COLLISION_RADIUS && heading.angle(Direction.WEST) < halfPi)
				return false;
			if (x >= Field.WIDTH - Player.COLLISION_RADIUS && heading.angle(Direction.EAST) < halfPi)
				return false;
			if (y <= Player.COLLISION_RADIUS && heading.angle(Direction.SOUTH) < halfPi)
				return false;
			if (y >= Field.DEPTH - Player.COLLISION_RADIUS && heading.angle(Direction.NORTH) < halfPi)
				return false;
			if (isAgainstAnyTable(field, player, true))
				return false;
			return true;
		case MOVE_BACKWARD:
			if (x <= Player.COLLISION_RADIUS && heading.angle(Direction.EAST) < halfPi)
				return false;
			if (x >= Field.WIDTH - Player.COLLISION_RADIUS && heading.angle(Direction.WEST) < halfPi)
				return false;
			if (y <= Player.COLLISION_RADIUS && heading.angle(Direction.SOUTH) < halfPi)
				return false;
			if (y >= Field.DEPTH - Player.COLLISION_RADIUS && heading.angle(Direction.NORTH) < halfPi)
				return false;
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

	public static boolean canPickup(Player player, Food food, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(food, "food cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		if (food.getHeight() > 0.0)
			return false;

		Vector playerLocation = player.getLocation();
		Vector foodLocation = food.getLocation();

		double deltaX = foodLocation.getX() - playerLocation.getX();
		double deltaY = foodLocation.getY() - playerLocation.getY();
		double distanceSquared = deltaX * deltaX + deltaY * deltaY;
		if (distanceSquared > Player.REACH_DISTANCE * Player.REACH_DISTANCE)
			return false;

		Direction toFood = Vector.cartesian(deltaX, deltaY).getDirection();
		return getArmDirection(player, hand).angle(toFood) < Player.REACH_RANGE / 2;

	}

	public static boolean isAgainstTable(Player player, Table table, boolean movingForward) {

		Vector location = player.getLocation();
		double playerX = location.getX();
		double playerY = location.getY();
		Direction heading = player.getHeading();
		double halfPi = Math.PI / 2.0;

		boolean movingNorth, movingSouth, movingEast, movingWest;
		if (movingForward) {
			movingNorth = heading.angle(Direction.NORTH) < halfPi;
			movingSouth = heading.angle(Direction.SOUTH) < halfPi;
			movingEast = heading.angle(Direction.EAST) < halfPi;
			movingWest = heading.angle(Direction.WEST) < halfPi;
		} else {
			movingNorth = heading.angle(Direction.NORTH) > halfPi;
			movingSouth = heading.angle(Direction.SOUTH) > halfPi;
			movingEast = heading.angle(Direction.EAST) > halfPi;
			movingWest = heading.angle(Direction.WEST) > halfPi;
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

	public static final Vector getArmLocation(Player player, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		return player.getLocation().add(Vector.polar(Player.REACH_DISTANCE, getArmDirection(player, hand)));

	}

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

	public static double getMoveMultiplier(double energy) {
		return Math.exp(energy / 100.0 - 1);
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
