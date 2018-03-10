package org.bitbrawl.foodfight.engine.field;

import java.util.Objects;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicFood implements Food, Dynamic<FoodState> {

	private FoodState state;

	public DynamicFood(FoodState state) {
		this.state = state;
	}

	@Override
	public Type getType() {
		return state.getType();
	}

	@Override
	public Vector getLocation() {
		return state.getLocation();
	}

	@Override
	public double getHeight() {
		return state.getHeight();
	}

	@Override
	public Direction getHeading() {
		return state.getHeading();
	}

	@Override
	public FoodState getState() {
		return state;
	}

	@Override
	public void update(FoodState state) {
		Objects.requireNonNull(state);
		if (!this.state.getType().equals(state.getType()))
			throw new IllegalArgumentException("This food's type cannot change");
		this.state = state;
	}

}
