package org.bitbrawl.foodfight.state;

import java.io.Serializable;

import org.bitbrawl.foodfight.field.FoodType;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class FoodState implements Serializable {

	private final FoodType type;
	private final Vector location;
	private final double height;
	private final Direction heading;

	public FoodState(FoodType type, Vector location, double height, Direction heading) {
		this.location = location;
		this.height = height;
		this.type = type;
		this.heading = heading;
	}

	public FoodType getType() {
		return type;
	}

	public Vector getLocation() {
		return location;
	}

	public double getHeight() {
		return height;
	}

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

	private static final long serialVersionUID = 1L;

}
