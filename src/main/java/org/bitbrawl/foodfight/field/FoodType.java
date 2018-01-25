package org.bitbrawl.foodfight.field;

import java.util.concurrent.ThreadLocalRandom;

import net.jcip.annotations.Immutable;

@Immutable
public enum FoodType {

	APPLE("Apple", 20.0, 2.5), BANANA("Banana", 20.0, 2.0), SANDWICH("Sandwich", 20.0, 4.5), PIE("Pie", 50.0, 5.5);

	private final String name;
	private final double radius;
	private final double health;

	private FoodType(String name, double radius, double health) {
		this.name = name;
		this.radius = radius;
		this.health = health;
	}

	public static FoodType random() {
		FoodType[] values = values();
		return values[ThreadLocalRandom.current().nextInt(values.length)];
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
