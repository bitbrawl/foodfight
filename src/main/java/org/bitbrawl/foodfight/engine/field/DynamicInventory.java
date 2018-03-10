package org.bitbrawl.foodfight.engine.field;

import org.bitbrawl.foodfight.field.Food.Type;
import org.bitbrawl.foodfight.field.Inventory;
import org.bitbrawl.foodfight.field.Player.Hand;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicInventory implements Inventory, Dynamic<InventoryState> {

	private InventoryState state;

	public DynamicInventory(InventoryState state) {
		this.state = state;
	}

	@Override
	public Type get(Hand hand) {
		return state.get(hand);
	}

	@Override
	public InventoryState getState() {
		return state;
	}

	@Override
	public void update(InventoryState state) {
		this.state = state;
	}

}
