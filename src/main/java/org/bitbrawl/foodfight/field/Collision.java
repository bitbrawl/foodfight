package org.bitbrawl.foodfight.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bitbrawl.foodfight.random.RandomScalar;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public class Collision {

	private final Vector location;
	private final Collection<FieldElement> objects;

	public Collision(Vector location, Collection<? extends FieldElement> objects) {
		this.location = location;
		this.objects = Collections.unmodifiableList(new ArrayList<>(objects));
	}

	public Vector getLocation() {
		return location;
	}

	public Collection<FieldElement> getObjects() {
		return objects;
	}

	public static final RandomScalar KNOCKBACK = new RandomScalar(20.0, 5.0);

}
