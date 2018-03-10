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

		APPLE("Apple", 20.0, new RandomScalar(5.0, 0.2), new RandomScalar(5.0, 1.0)),
		BANANA("Banana", 20.0, new RandomScalar(5.0, 1.0), new RandomScalar(5.0, 1.0)),
		RASPBERRY("Raspberry", 10.0, new RandomScalar(2.0, 0.5), new RandomScalar(5.0, 1.0)),
		BROCCOLI("Broccoli", 20.0, new RandomScalar(10.0, 1.0), new RandomScalar(2.0, 0.5)),
		MILK("Milk", 20.0, new RandomScalar(5.0, 1.0), new RandomScalar(5.0, 1.0)),
		CHOCOLATE("Chocolate", 20.0, new RandomScalar(10.0, 1.0), new RandomScalar(5.0, 1.0)),
		SANDWICH("Sandwich", 20.0, new RandomScalar(10.0, 2.0), new RandomScalar(5.0, 1.0)),
		PIE("Pie", 50.0, new RandomScalar(10.0, 1.0), new RandomScalar(20.0, 1.0));

		private final String name;
		private final double radius;
		private final RandomScalar health;
		private final RandomScalar damage;

		private Type(String name, double radius, RandomScalar health, RandomScalar damage) {
			this.name = name;
			this.radius = radius;
			this.health = health;
			this.damage = damage;
		}

		@Override
		public String toString() {
			return name;
		}

		public double getRadius() {
			return radius;
		}

		public RandomScalar getHealth() {
			return health;
		}

		public RandomScalar getDamage() {
			return damage;
		}

	}

}
