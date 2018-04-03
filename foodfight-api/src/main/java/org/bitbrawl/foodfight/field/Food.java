package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

import com.google.gson.annotations.SerializedName;

public interface Food extends Locatable {

	public Type getType();

	public double getHeight();

	public Direction getHeading();

	public static final RandomScalar SPEED = new RandomScalar(20.0, 1.0);
	public static final RandomScalar FALL_SPEED = new RandomScalar(2.0, 1.0);

	public enum Type {

		@SerializedName("apple")
		APPLE("Apple", 20.0, new RandomScalar(5.0, 0.2), new RandomScalar(5.0, 1.0)),
		@SerializedName("banana")
		BANANA("Banana", 20.0, new RandomScalar(5.0, 1.0), new RandomScalar(5.0, 1.0)),
		@SerializedName("raspberry")
		RASPBERRY("Raspberry", 10.0, new RandomScalar(2.0, 0.5), new RandomScalar(5.0, 1.0)),
		@SerializedName("broccoli")
		BROCCOLI("Broccoli", 20.0, new RandomScalar(10.0, 1.0), new RandomScalar(2.0, 0.5)),
		@SerializedName("milk")
		MILK("Milk", 20.0, new RandomScalar(5.0, 1.0), new RandomScalar(5.0, 1.0)),
		@SerializedName("chocolate")
		CHOCOLATE("Chocolate", 20.0, new RandomScalar(10.0, 1.0), new RandomScalar(5.0, 1.0)),
		@SerializedName("sandwich")
		SANDWICH("Sandwich", 20.0, new RandomScalar(10.0, 2.0), new RandomScalar(5.0, 1.0)),
		@SerializedName("pie")
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
