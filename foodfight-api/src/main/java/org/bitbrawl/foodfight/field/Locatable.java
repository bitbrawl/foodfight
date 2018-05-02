package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Vector;

/**
 * An object that can be located on the field, either a {@link Player},
 * {@link Food}, or {@link Table}. For all of these objects, the location refers
 * to their center point.
 * 
 * @author Finn
 */
public interface Locatable {

	/**
	 * Returns a vector representing this object's location on the field. When
	 * viewing the field from above, the x-coordinate ({@link Vector#getX()})
	 * represents the horizontal distance from the left edge, and the
	 * y-coordinate ({@link Vector#getY()}) represents the horizontal distance
	 * from the bottom edge.
	 * 
	 * @return this object's location on the field
	 */
	Vector getLocation();

}
