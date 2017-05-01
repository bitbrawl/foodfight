package org.bitbrawl.foodfight.field;

import java.util.Collection;

import org.bitbrawl.foodfight.util.Vector;

public interface Collision {

	public Vector getLocation();

	public Collection<? extends FieldElement> objects();

	public static final double KNOCKBACK = 20.0;

}
