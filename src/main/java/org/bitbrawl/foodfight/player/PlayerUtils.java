package org.bitbrawl.foodfight.player;

import java.util.Objects;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.state.FieldState;
import org.bitbrawl.foodfight.state.FoodState;
import org.bitbrawl.foodfight.state.PlayerState;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class PlayerUtils {

	private PlayerUtils() {
		throw new AssertionError("PlayerUtils is not instantiable");
	}

	public boolean isValidAction(FieldState field, PlayerState player, Action action) {
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

		switch (action) {
		case MOVE_FORWARD:
			if (x <= 0 && heading.angle(Direction.WEST) <= 90.0)
				return false;
			if (x >= Field.WIDTH && heading.angle(Direction.EAST) <= 90.0)
				return false;
			if (y <= 0 && heading.angle(Direction.NORTH) <= 90.0)
				return false;
			if (y >= Field.DEPTH && heading.angle(Direction.SOUTH) <= 90.0)
				return false;
			return true;
		case MOVE_BACKWARD:
			if (x <= 0 && heading.angle(Direction.EAST) <= 90.0)
				return false;
			if (x >= Field.WIDTH && heading.angle(Direction.WEST) <= 90.0)
				return false;
			if (y <= 0 && heading.angle(Direction.SOUTH) <= 90.0)
				return false;
			if (y >= Field.DEPTH && heading.angle(Direction.NORTH) <= 90.0)
				return false;
			return true;
		case PICKUP_LEFT:
			if (inventory.get(Hand.LEFT) != null)
				return false;
			for (FoodState food : field.getFood())
				if (canPickup(player, food, Hand.LEFT))
					return true;
			return false;
		case PICKUP_RIGHT:
			if (inventory.get(Hand.RIGHT) != null)
				return false;
			for (FoodState food : field.getFood())
				if (canPickup(player, food, Hand.RIGHT))
					return true;
			return false;
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

	public boolean canPickup(PlayerState player, FoodState food, Hand hand) {
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

		Direction toFood = new Direction(Vector.cartesian(deltaX, deltaY));
		return getArmDirection(player, hand).angle(toFood) < Player.REACH_RANGE / 2;

	}

	public final Vector getArmLocation(PlayerState player, Hand hand) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		return player.getLocation().add(Vector.polar(Player.REACH_DISTANCE, getArmDirection(player, hand)));

	}

	public final Direction getArmDirection(PlayerState player, Hand hand) {
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

}
