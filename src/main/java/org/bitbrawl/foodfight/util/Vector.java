package org.bitbrawl.foodfight.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

import net.jcip.annotations.Immutable;

@Immutable
public final class Vector implements Serializable {

	private final double x;
	private final double y;

	private transient volatile double magnitude;
	private transient volatile Direction direction;

	private Vector(double x, double y, double magnitude, Direction direction) {
		this.x = x;
		this.y = y;
		this.magnitude = magnitude;
		this.direction = direction;
	}

	public static Vector cartesian(double x, double y) {
		double magnitude = Math.hypot(x, y);
		Direction direction = new Direction(Math.atan2(y, x));
		return new Vector(x, y, magnitude, direction);
	}

	public static Vector polar(double magnitude, Direction direction) {
		if (magnitude < 0.0)
			throw new IllegalArgumentException("magnitude cannot be negative");
		Objects.requireNonNull(direction, "direction cannot be null");

		double x = magnitude * Math.sin(Math.toRadians(direction.get()));
		double y = -magnitude * Math.cos(Math.toRadians(direction.get()));
		return new Vector(x, y, magnitude, direction);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public Direction getDirection() {
		return direction;
	}

	public Vector getOpposite() {
		return new Vector(-x, -y, magnitude, direction.getOpposite());
	}

	public Vector add(Vector other) {
		return cartesian(x + other.x, y + other.y);
	}

	public Vector subtract(Vector other) {
		return cartesian(x - other.x, y - other.y);
	}

	public Vector multiply(double scalar) {
		return polar(magnitude * scalar, direction);
	}

	public Vector divide(double scalar) {
		return polar(magnitude / scalar, direction);
	}

	public double dot(Vector other) {
		return x * other.x + y * other.y;
	}

	public static Vector average(Vector vectorA, Vector vectorB) {
		return vectorA.add(vectorB).divide(2.0);
	}

	public double componentAlong(Vector other) {
		return dot(other) / other.magnitude;
	}

	@Override
	public String toString() {
		return "<" + Math.round(x) + ", " + Math.round(y) + ">";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector))
			return false;

		Vector vector = (Vector) other;
		return x == vector.x && y == vector.y;

	}

	@Override
	public int hashCode() {
		return Double.hashCode(x) * 31 + Double.hashCode(y);
	}

	public static final Vector ZERO = new Vector(0.0, 0.0, 0.0, new Direction(0.0));

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		magnitude = Math.hypot(x, y);
		direction = new Direction(Math.atan2(y, x));

	}

	private static final long serialVersionUID = 9088292157961730829L;

}
