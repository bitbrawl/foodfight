package org.bitbrawl.foodfight.engine.match;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.PlayerState;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.jcip.annotations.Immutable;

@Immutable
public final class MatchHistory {

	private final int matchNumber;
	private final Map<Character, String> names;
	private final List<FieldState> fieldStates;

	public MatchHistory(int matchNumber, CharFunction<String> names, List<? extends FieldState> fieldStates) {
		this.matchNumber = matchNumber;
		this.fieldStates = Collections.unmodifiableList(new ArrayList<>(fieldStates));
		Map<Character, String> tempNames = new LinkedHashMap<>();
		for (PlayerState player : fieldStates.get(fieldStates.size() - 1).getPlayerStates()) {
			char symbol = player.getSymbol();
			tempNames.put(symbol, names.apply(symbol));
		}
		this.names = Collections.unmodifiableMap(tempNames);
	}

	public int getMatchNumber() {
		return matchNumber;
	}

	public Map<Character, String> getNames() {
		return names;
	}

	public List<FieldState> getFieldStates() {
		return fieldStates;
	}

	public FieldState getFinalState() {
		return fieldStates.get(fieldStates.size() - 1);
	}

	public enum Deserializer implements JsonDeserializer<MatchHistory> {

		INSTANCE;

		@Override
		public MatchHistory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			int matchNumber = object.getAsJsonPrimitive("matchNumber").getAsInt();
			Type namesType = new TypeToken<Map<Character, String>>() {
			}.getType();
			Map<Character, String> names = context.deserialize(object.get("names"), namesType);
			Type statesType = new TypeToken<List<FieldState>>() {
			}.getType();
			List<FieldState> states = context.deserialize(object.get("states"), statesType);

			return new MatchHistory(matchNumber, names::get, states);

		}

	}

}
