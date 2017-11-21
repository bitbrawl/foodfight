package org.bitbrawl.foodfight.state;

import org.bitbrawl.foodfight.field.FoodPiece;
import org.bitbrawl.foodfight.field.FoodType;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class FoodState implements FoodPiece {

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

}
