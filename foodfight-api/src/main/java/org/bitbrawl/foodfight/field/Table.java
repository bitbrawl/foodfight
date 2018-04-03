package org.bitbrawl.foodfight.field;

import java.util.Set;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public interface Table {

	public Vector getLocation();

	public Set<Food.Type> getFood();

	public default double getEdge(Direction side) {

		if (side.equals(Direction.NORTH))
			return getLocation().getY() + RADIUS;

		if (side.equals(Direction.SOUTH))
			return getLocation().getY() - RADIUS;

		if (side.equals(Direction.EAST))
			return getLocation().getX() + RADIUS;

		if (side.equals(Direction.WEST))
			return getLocation().getX() - RADIUS;

		throw new IllegalArgumentException("side must be NORTH, SOUTH, EAST, or WEST");

	}

	public static final double RADIUS = 100.0;

}
