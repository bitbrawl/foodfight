package org.bitbrawl.foodfight.engine.field;

import java.util.Objects;
import java.util.Set;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicTable implements Table, Dynamic<TableState> {

	private TableState state;

	public DynamicTable(TableState state) {
		this.state = state;
	}

	@Override
	public Vector getLocation() {
		return state.getLocation();
	}

	@Override
	public Set<Food.Type> getFood() {
		return state.getFood();
	}

	@Override
	public TableState getState() {
		return state;
	}

	@Override
	public void update(TableState state) {
		Objects.requireNonNull(state, "state cannot be null");
		if (!this.state.getLocation().equals(state.getLocation()))
			throw new IllegalArgumentException("state location must match");
		this.state = state;
	}

}
