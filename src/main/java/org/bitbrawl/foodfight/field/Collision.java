package org.bitbrawl.foodfight.field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bitbrawl.foodfight.util.RandomScalar;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public class Collision implements Serializable {

	private final Vector location;
	private Set<FieldElement> objects;

	public Collision(Vector location, Set<? extends FieldElement> objects) {
		this.location = location;
		this.objects = Collections.unmodifiableSet(new LinkedHashSet<>(objects));
	}

	public Vector getLocation() {
		return location;
	}

	public Set<FieldElement> getObjects() {
		return objects;
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		objects = Collections.unmodifiableSet(new LinkedHashSet<>(objects));
	}

	public static final RandomScalar KNOCKBACK = new RandomScalar(20.0, 5.0);

	private static final long serialVersionUID = 1L;

}
