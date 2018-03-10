package org.bitbrawl.foodfight.engine.field;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Inventory;
import org.bitbrawl.foodfight.field.Player;

import net.jcip.annotations.Immutable;

@Immutable
public class InventoryState implements Inventory {

	private final Map<Player.Hand, Food.Type> map;

	public InventoryState(Map<Player.Hand, ? extends Food.Type> map) {
		this.map = new EnumMap<>(map);
	}

	@SuppressWarnings("unused")
	private InventoryState() {
		map = null;
	}

	@Override
	public Food.Type get(Player.Hand hand) {
		return map.get(hand);
	}

	public InventoryState add(Player.Hand hand, Food.Type food) {
		Objects.requireNonNull(hand);
		Objects.requireNonNull(food);

		Map<Player.Hand, Food.Type> newMap = new EnumMap<>(map);
		newMap.put(hand, food);
		return new InventoryState(newMap);

	}

	public InventoryState remove(Player.Hand hand) {
		Objects.requireNonNull(hand);

		Map<Player.Hand, Food.Type> newMap = new EnumMap<>(map);
		newMap.remove(hand);
		return new InventoryState(newMap);

	}

}
