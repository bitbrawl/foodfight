package org.bitbrawl.foodfight.engine.match;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bitbrawl.foodfight.engine.field.FieldState;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import net.jcip.annotations.Immutable;

@Immutable
public final class MatchHistory {

	private final List<FieldState> fieldStates;

	public MatchHistory(List<? extends FieldState> fieldStates) {
		this.fieldStates = Collections.unmodifiableList(new ArrayList<>(fieldStates));
	}

	public List<FieldState> getFieldStates() {
		return fieldStates;
	}

	public FieldState getFinalState() {
		return fieldStates.get(fieldStates.size() - 1);
	}

	public enum Serializer implements JsonSerializer<MatchHistory> {
		INSTANCE;

		@Override
		public JsonElement serialize(MatchHistory src, Type typeOfSrc, JsonSerializationContext context) {
			Type type = new TypeToken<List<FieldState>>() {
			}.getType();
			return context.serialize(src.fieldStates, type);
		}

	}

	public enum Deserializer implements JsonDeserializer<MatchHistory> {
		INSTANCE;

		@Override
		public MatchHistory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Type type = new TypeToken<List<FieldState>>() {
			}.getType();
			return new MatchHistory(context.deserialize(json, type));
		}

	}

}
