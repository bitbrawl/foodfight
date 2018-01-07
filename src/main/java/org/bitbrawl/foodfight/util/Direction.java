package org.bitbrawl.foodfight.util;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import net.jcip.annotations.Immutable;

@Immutable
public final class Direction implements Serializable {

	private final double direction;

	public Direction() {
		this(ThreadLocalRandom.current().nextDouble() * TWO_PI);
	}

	public Direction(double direction) {
		this.direction = fixAngle(direction);
	}

	public double get() {
		return direction;
	}

	public Direction add(double angle) {
		return new Direction(direction + angle);
	}

	public double angle(Direction other) {
		double result = fixAngle(other.direction - direction);
		if (result > Math.PI)
			result = TWO_PI - result;
		return result;
	}

	public Direction getOpposite() {
		return add(Math.PI);
	}

	public Direction flipX() {
		return new Direction(TWO_PI - direction);
	}

	public Direction flipY() {
		return new Direction(Math.PI - direction);
	}

	@Override
	public String toString() {
		return Math.round(direction) + "\u00b0";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Direction)
			return direction == ((Direction) other).direction;
		return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(direction);
	}

	public static final Direction EAST = new Direction(0.0);
	public static final Direction NORTH = new Direction(0.5 * Math.PI);
	public static final Direction SOUTH = new Direction(Math.PI);
	public static final Direction WEST = new Direction(1.5 * Math.PI);

	private static double fixAngle(double angle) {
		double remainder = Math.IEEEremainder(angle, TWO_PI);
		if (remainder < 0.0)
			remainder += TWO_PI;
		return remainder;
	}

	private static final double TWO_PI = 2.0 * Math.PI;

	private static final long serialVersionUID = 1L;

}
