package org.bitbrawl.foodfight.engine.field;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class TableState implements Table {

	private final Vector location;
	private final Set<Food.Type> food;

	public TableState(Vector location, Set<Food.Type> food) {
		this.location = location;
		this.food = Collections.unmodifiableSet(EnumSet.copyOf(food));
	}

	@SuppressWarnings("unused")
	private TableState() {
		location = null;
		food = null;
	}

	@Override
	public Vector getLocation() {
		return location;
	}

	@Override
	public Set<Food.Type> getFood() {
		return food;
	}

}
