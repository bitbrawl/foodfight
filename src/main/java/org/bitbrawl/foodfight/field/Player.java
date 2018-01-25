package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

public interface Player extends Locatable {

	public char getSymbol();

	public double getHeight();

	public Direction getHeading();

	public Inventory getInventory();

	public double getHealth();

	public static final double HEIGHT = 200.0;
	public static final double MIN_HEIGHT = 100.0;
	public static final RandomScalar THROW_HEIGHT = new RandomScalar(150.0, 10.0);
	public static final RandomScalar FORWARD_MOVEMENT_SPEED = new RandomScalar(10.0, 1.0);
	public static final RandomScalar REVERSE_MOVEMENT_SPEED = new RandomScalar(5.0, 1.0);
	public static final RandomScalar DUCK_SPEED = new RandomScalar(5.0, 1.0);
	public static final RandomScalar TURN_SPEED = new RandomScalar(5.0, 1.0);
	public static final double REACH_DISTANCE = 100.0;
	public static final double REACH_RANGE = 60.0;
	public static final double LEFT_ARM_DIRECTION = -30.0;
	public static final double RIGHT_ARM_DIRECTION = 30.0;
	public static final double COLLISION_RADIUS = 50.0;
	public static final double MAX_HEALTH = 100.0;

	public enum Hand {

		LEFT("Left hand"), RIGHT("Right hand");

		private final String name;

		private Hand(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
