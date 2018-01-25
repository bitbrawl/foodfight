package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

public interface Food extends Locatable {

	public Type getType();

	public double getHeight();

	public Direction getHeading();

	public static final RandomScalar SPEED = new RandomScalar(20.0, 1.0);
	public static final RandomScalar FALL_SPEED = new RandomScalar(2.0, 1.0);

	public enum Type {

		APPLE("Apple", 20.0, 2.5), BANANA("Banana", 20.0, 2.0), SANDWICH("Sandwich", 20.0, 4.5), PIE("Pie", 50.0, 5.5);

		private final String name;
		private final double radius;
		private final double health;

		private Type(String name, double radius, double health) {
			this.name = name;
			this.radius = radius;
			this.health = health;
		}

		@Override
		public String toString() {
			return name;
		}

		public double getRadius() {
			return radius;
		}

		public double getHealth() {
			return health;
		}

	}

}
