package org.bitbrawl.foodfight.engine.field;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.util.Vector;

public final class CollisionState implements Collision {

	private final Vector location;
	private final double damage;

	public CollisionState(Vector location, double damage) {
		this.location = location;
		this.damage = damage;
	}

	@SuppressWarnings("unused")
	private CollisionState() {
		location = null;
		damage = Double.NaN;
	}

	@Override
	public Vector getLocation() {
		return location;
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public String toString() {
		return "CollisionState[location=" + location + ",damage=" + damage + ']';
	}

}
