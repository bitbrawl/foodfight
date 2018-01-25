package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bitbrawl.foodfight.field.FoodType;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class TableState implements Serializable {

	private final Vector location;
	private Set<FoodType> food;

	public TableState(Vector location, Set<FoodType> food) {
		this.location = location;
		this.food = Collections.unmodifiableSet(EnumSet.copyOf(food));
	}

	public Vector getLocation() {
		return location;
	}

	public Set<FoodType> getFood() {
		return food;
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		food = Collections.unmodifiableSet(EnumSet.copyOf(food));

	}

	private static final long serialVersionUID = 1L;

}
