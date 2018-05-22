package org.bitbrawl.foodfight.util;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Inventory;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Player.Hand;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PlayerUtilsTest {

	@Test
	void testGetArmDirection() {

		Vector location = Vector.cartesian(326, 940);
		Direction heading = new Direction(3.35);
		Player player = new Player() {

			@Override
			public Vector getLocation() {
				return location;
			}

			@Override
			public char getSymbol() {
				return '0';
			}

			@Override
			public Inventory getInventory() {
				return null;
			}

			@Override
			public double getHeight() {
				return Player.HEIGHT;
			}

			@Override
			public Direction getHeading() {
				return heading;
			}

			@Override
			public double getEnergy() {
				return 0;
			}
		};

		Direction leftArmDirection = PlayerUtils.getArmDirection(player, Hand.LEFT);
		Direction expected = new Direction(3.35 + Math.PI / 6);

		Assertions.assertEquals(expected.get(), leftArmDirection.get(), 1e-5);

	}

	@Test
	void testCanPickup() {

		Vector location = Vector.cartesian(326, 940);
		Direction heading = new Direction(3.35);
		Player player = new Player() {

			@Override
			public Vector getLocation() {
				return location;
			}

			@Override
			public char getSymbol() {
				return '0';
			}

			@Override
			public Inventory getInventory() {
				return null;
			}

			@Override
			public double getHeight() {
				return Player.HEIGHT;
			}

			@Override
			public Direction getHeading() {
				return heading;
			}

			@Override
			public double getEnergy() {
				return 0;
			}
		};

		Food food = new Food() {

			@Override
			public Vector getLocation() {
				return Vector.cartesian(325.7355405972706, 939.8316727970221);
			}

			@Override
			public Type getType() {
				return Type.MILK;
			}

			@Override
			public double getHeight() {
				return 0;
			}

			@Override
			public Direction getHeading() {
				return Direction.random();
			}
		};

		Assertions.assertTrue(PlayerUtils.canPickup(player, food, Hand.LEFT));

	}

}
