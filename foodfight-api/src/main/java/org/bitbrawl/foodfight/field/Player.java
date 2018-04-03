package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

import com.google.gson.annotations.SerializedName;

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
	public static final RandomScalar TURN_SPEED = new RandomScalar(0.05, 0.01);
	public static final double REACH_DISTANCE = 100.0;
	public static final double REACH_RANGE = 60.0;
	public static final double LEFT_ARM_DIRECTION = -Math.PI / 6.0;
	public static final double RIGHT_ARM_DIRECTION = Math.PI / 6.0;
	public static final double COLLISION_RADIUS = 50.0;
	public static final double MAX_HEALTH = 100.0;
	public static final RandomScalar COLLISION_DAMAGE = new RandomScalar(2.0, 1.0);

	public enum Hand {

		@SerializedName("left")
		LEFT("Left hand"),
		@SerializedName("right")
		RIGHT("Right hand");

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
