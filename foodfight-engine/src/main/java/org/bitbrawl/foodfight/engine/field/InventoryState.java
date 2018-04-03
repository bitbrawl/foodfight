package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Inventory;
import org.bitbrawl.foodfight.field.Player;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import net.jcip.annotations.Immutable;

@Immutable
public class InventoryState implements Inventory {

	private final Map<Player.Hand, Food.Type> map;

	public InventoryState(Map<Player.Hand, Food.Type> map) {
		this.map = new EnumMap<>(Player.Hand.class);
		this.map.putAll(map);
	}

	public static InventoryState fromInventory(Inventory inventory) {
		if (inventory instanceof InventoryState)
			return (InventoryState) inventory;
		if (inventory instanceof DynamicInventory)
			return ((DynamicInventory) inventory).getState();
		Map<Player.Hand, Food.Type> map = new EnumMap<>(Player.Hand.class);
		for (Player.Hand hand : Player.Hand.values()) {
			Food.Type type = inventory.get(hand);
			if (type != null)
				map.put(hand, type);
		}
		return new InventoryState(map);
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

	@Override
	public String toString() {
		return map.toString();
	}

	public enum Serializer implements JsonSerializer<InventoryState> {
		INSTANCE;

		@Override
		public JsonElement serialize(InventoryState src, Type typeOfSrc, JsonSerializationContext context) {
			Type type = new TypeToken<Map<Player.Hand, Food.Type>>() {
			}.getType();
			return context.serialize(src.map, type);
		}

	}

	public enum Deserializer implements JsonDeserializer<InventoryState> {
		INSTANCE;

		@Override
		public InventoryState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Type type = new TypeToken<Map<Player.Hand, Food.Type>>() {
			}.getType();
			return new InventoryState(context.deserialize(json, type));
		}

	}

}
