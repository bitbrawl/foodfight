package org.bitbrawl.foodfight.field;

import java.util.Set;

import org.bitbrawl.foodfight.util.Vector;

public interface Table {

	public Vector getLocation();

	public Set<Food.Type> getFood();

	public static final double RADIUS = 100.0;

}
