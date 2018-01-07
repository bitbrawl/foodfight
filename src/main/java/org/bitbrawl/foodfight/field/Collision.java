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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Collision))
			return false;
		Collision collision = (Collision) o;
		return location.equals(collision.location) && objects.equals(collision.objects);
	}

	@Override
	public int hashCode() {
		return location.hashCode() * 31 + objects.hashCode();
	}

	@Override
	public String toString() {
		return "Collision[location=" + location + ",objects=" + objects + "]";
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		objects = Collections.unmodifiableSet(new LinkedHashSet<>(objects));
	}

	public static final RandomScalar KNOCKBACK = new RandomScalar(20.0, 5.0);

	private static final long serialVersionUID = 1L;

}
