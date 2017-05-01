package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public final class FieldElementState {

	private final Vector location;
	private final double height;
	private final Direction heading;

	public FieldElementState(Vector location, double height, Direction heading) {

		this.location = location;
		this.height = height;
		this.heading = heading;

	}

	@SuppressWarnings("unused")
	private FieldElementState() {
		location = null;
		height = 0.0;
		heading = null;
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
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("{position=");
		builder.append(location);
		builder.append(" height=");
		builder.append(Math.round(height));
		builder.append(" heading=");
		builder.append(heading);
		builder.append('}');

		return builder.toString();

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FieldElementState))
			return false;
		FieldElementState state = (FieldElementState) obj;
		return location == state.location && height == state.height && heading == state.heading;
	}

	@Override
	public int hashCode() {

		int result = 17;

		result = 31 * result + location.hashCode();
		result = 31 * result + Double.hashCode(height);
		result = 31 * result + heading.hashCode();

		return result;

	}
}
