package org.bitbrawl.foodfight.field;

import java.util.Set;

import org.bitbrawl.foodfight.state.TableState;
import org.bitbrawl.foodfight.util.Vector;

public interface Table {

	public Vector getLocation();

	public Set<FoodType> getFood();

	public TableState getState();

	public static final double COLLISION_RADIUS = 100.0;

}
