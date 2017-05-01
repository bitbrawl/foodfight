package org.bitbrawl.foodfight.field;

public enum FoodType {

	APPLE("Apple", 2.5), BANANA("Banana", 2.0), SANDWICH("Sandwich", 4.5), PIE("Pie", 5.5);

	private final String name;
	private final double health;

	private FoodType(String name, double health) {
		this.name = name;
		this.health = health;
	}

	@Override
	public String toString() {
		return name;
	}

	public double getHealth() {
		return health;
	}

}
