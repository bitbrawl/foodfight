package org.bitbrawl.foodfight.util;

import java.util.concurrent.ThreadLocalRandom;

public final class Direction {

	private final double direction;

	public Direction() {
		this(ThreadLocalRandom.current().nextDouble() * DEGREES_PER_ROTATION);
	}

	public Direction(double direction) {
		this.direction = fixAngle(direction);
	}

	public Direction(Vector direction) {
		this(Math.toDegrees(Math.atan2(direction.getX(), -direction.getY())));
	}

	public double get() {
		return direction;
	}

	public Direction add(double angle) {
		return new Direction(direction + angle);
	}

	public double subtract(Direction other) {
		double result = fixAngle(direction - other.direction);
		if (result > 180.0)
			result -= 360.0;
		return result;
	}

	public double angle(Direction other) {
		double result = fixAngle(other.direction - direction);
		if (result > 180.0)
			result = 360 - result;
		return result;
	}

	public Direction getOpposite() {
		return add(DEGREES_PER_ROTATION / 2.0);
	}

	public Direction flipX() {
		return new Direction(360.0 - direction);
	}

	public Direction flipY() {
		return new Direction(180.0 - direction);
	}

	@Override
	public String toString() {
		return Math.round(direction) + "°";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Direction)
			return direction == ((Direction) other).direction;
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		long l = Double.doubleToLongBits(direction);
		int c = (int) (l ^ (l >>> 32));
		result = 31 * result + c;
		return result;
	}

	public static final Direction NORTH = new Direction(0.0);
	public static final Direction EAST = new Direction(90.0);
	public static final Direction SOUTH = new Direction(180.0);
	public static final Direction WEST = new Direction(270.0);

	private static double fixAngle(double angle) {
		while (angle < 0.0)
			angle += DEGREES_PER_ROTATION;
		while (angle >= DEGREES_PER_ROTATION)
			angle -= DEGREES_PER_ROTATION;
		return angle;
	}

	private static final double DEGREES_PER_ROTATION = 360.0;

}
