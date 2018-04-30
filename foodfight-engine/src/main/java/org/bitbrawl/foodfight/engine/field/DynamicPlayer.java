package org.bitbrawl.foodfight.engine.field;

import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicPlayer implements Player, Dynamic<PlayerState> {

	private PlayerState state;
	private DynamicInventory inventory;

	public DynamicPlayer(PlayerState state) {
		this.state = state;
		this.inventory = new DynamicInventory(state.getInventory());
	}

	@Override
	public char getSymbol() {
		return state.getSymbol();
	}

	@Override
	public Vector getLocation() {
		return state.getLocation();
	}

	@Override
	public double getHeight() {
		return state.getHeight();
	}

	@Override
	public Direction getHeading() {
		return state.getHeading();
	}

	@Override
	public DynamicInventory getInventory() {
		return inventory;
	}

	@Override
	public double getEnergy() {
		return state.getEnergy();
	}

	@Override
	public PlayerState getState() {
		return state;
	}

	@Override
	public void update(PlayerState state) {
		if (this.state.getSymbol() != state.getSymbol())
			throw new IllegalArgumentException("This player's symbol cannot change");
		this.state = state;
		this.inventory.update(state.getInventory());
	}

	@Override
	public String toString() {
		return "DynamicPlayer[symbol=" + state.getSymbol() + ']';
	}

}
