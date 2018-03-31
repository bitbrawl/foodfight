package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.util.Vector;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.jcip.annotations.Immutable;

@Immutable
public final class TableState implements Table {

	private final Vector location;
	private final Set<Food.Type> food;

	public TableState(Vector location, Collection<Food.Type> food) {
		this.location = location;
		this.food = Collections.unmodifiableSet(EnumSet.copyOf(food));
	}

	public static TableState fromTable(Table table) {
		if (table instanceof TableState)
			return (TableState) table;
		if (table instanceof DynamicTable)
			return ((DynamicTable) table).getState();
		return new TableState(table.getLocation(), table.getFood());
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

	@Override
	public String toString() {
		return "TableState[location=" + location + ",food=" + food + ']';
	}

	public enum Deserializer implements JsonDeserializer<TableState> {
		INSTANCE;

		@Override
		public TableState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			Vector location = context.deserialize(object.getAsJsonObject("location"), Vector.class);
			Type foodType = new TypeToken<List<Food.Type>>() {
			}.getType();
			List<Food.Type> food = context.deserialize(object.getAsJsonArray("food"), foodType);
			return new TableState(location, food);

		}

	}

}
