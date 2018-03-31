package org.bitbrawl.foodfight.engine.field;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class FoodState implements Food {

	private final Food.Type type;
	private final Vector location;
	private final double height;
	private final Direction heading;

	public FoodState(Food.Type type, Vector location, double height, Direction heading) {
		this.location = location;
		this.height = height;
		this.type = type;
		this.heading = heading;
	}

	public static FoodState fromFood(Food food) {
		if (food instanceof FoodState)
			return (FoodState) food;
		if (food instanceof DynamicFood)
			return ((DynamicFood) food).getState();
		return new FoodState(food.getType(), food.getLocation(), food.getHeight(), food.getHeading());
	}

	@SuppressWarnings("unused")
	private FoodState() {
		type = null;
		location = null;
		height = Double.NaN;
		heading = null;
	}

	@Override
	public Food.Type getType() {
		return type;
	}

	@Override
	public Vector getLocation() {
		return location;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public Direction getHeading() {
		return heading;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FoodState))
			return false;
		FoodState food = (FoodState) o;
		return location.equals(food.location) && height == food.height && type.equals(food.type)
				&& heading.equals(food.heading);
	}

	@Override
	public int hashCode() {
		int result = location.hashCode();
		result = result * 31 + Double.hashCode(height);
		result = result * 31 + type.hashCode();
		return result * 31 + heading.hashCode();
	}

	@Override
	public String toString() {
		return "FoodState[type=" + type + ",location=" + location + ",height=" + height + ",heading=" + heading + ']';
	}

}
