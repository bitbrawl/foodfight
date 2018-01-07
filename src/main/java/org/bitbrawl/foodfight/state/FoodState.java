package org.bitbrawl.foodfight.state;

import java.io.Serializable;

import org.bitbrawl.foodfight.field.FoodPiece;
import org.bitbrawl.foodfight.field.FoodType;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class FoodState implements FoodPiece, Serializable {

	private final Vector location;
	private final double height;
	private final FoodType type;

	public FoodState(Vector location, double height, FoodType type) {
		this.location = location;
		this.height = height;
		this.type = type;
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
	public FoodType getType() {
		return type;
	}

	@Override
	public FoodState getState() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FoodState))
			return false;
		FoodState food = (FoodState) o;
		return location.equals(food.location) && height == food.height && type.equals(food.type);
	}

	@Override
	public int hashCode() {
		int result = location.hashCode();
		result = result * 31 + Double.hashCode(height);
		return result * 31 + type.hashCode();
	}

	@Override
	public String toString() {
		return "FoodState[location=" + location + ",height=" + height + ",type=" + type + ']';
	}

	private static final long serialVersionUID = 1L;

}
