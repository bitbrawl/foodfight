package org.bitbrawl.foodfight.field;

import java.util.Objects;
import java.util.Set;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

/**
 * A team's table, where they can score points by putting food. For collision
 * purposes, you can think of a table as an infinitely tall square prism. The
 * square base is centered at the location specified by {@link #getLocation()},
 * and has a radius of {@link #RADIUS} (meaning a side length of 2 times
 * {@code RADIUS}). Any player running into an edge of the table will move
 * forward until they are at a location where they are "touching" the table.
 * Food pieces are moved onto a table when their center (specified by
 * {@link Food#getLocation()} is contained within the table.
 * 
 * @author Finn
 */
public interface Table extends Locatable {

	/**
	 * Returns a set of all of the types of food on this table. This set will be
	 * no bigger than {@link Field#MAX_FOOD}.
	 * 
	 * @return all of the types of food that are on this table
	 */
	public Set<Food.Type> getFood();

	/**
	 * Returns the edge of the table on the given side of the table. Note that
	 * the side must be {@link Direction#NORTH}, {@link Direction#EAST},
	 * {@link Direction#SOUTH}, or {@link Direction#WEST}. This method will
	 * return the y-value for the north or south edges, and the x-value for the
	 * east or west edges.
	 * <p>
	 * For example, if a controller wanted to get the x-coordinate of the
	 * eastern edge of the table, they could call
	 * {@code table.getEdge(Direction.EAST)}.
	 * 
	 * @param side
	 *            the side of the table to get the value of
	 * @return the x- or y-value of the given edge
	 * @throws NullPointerException
	 *             if side is null
	 * @throws IllegalArgumentException
	 *             if side is not NORTH, EAST, SOUTH, OR WEST
	 */
	public default double getEdge(Direction side) {
		Objects.requireNonNull(side, "side cannot be null");

		Vector location = getLocation();

		if (side.equals(Direction.NORTH))
			return location.getY() + RADIUS;

		if (side.equals(Direction.SOUTH))
			return location.getY() - RADIUS;

		if (side.equals(Direction.EAST))
			return location.getX() + RADIUS;

		if (side.equals(Direction.WEST))
			return location.getX() - RADIUS;

		throw new IllegalArgumentException("side must be NORTH, EAST, SOUTH, or WEST");

	}

	/** The distance from the center of any table to any of its edges. */
	public static final double RADIUS = 100.0;

}
