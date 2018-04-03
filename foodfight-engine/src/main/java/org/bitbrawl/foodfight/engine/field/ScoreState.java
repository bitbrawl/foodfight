package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import org.bitbrawl.foodfight.field.Event;
import org.bitbrawl.foodfight.field.Score;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public final class ScoreState implements Score {

	private final Map<Event, Integer> counts;
	private final int totalPoints;

	public ScoreState(Map<Event, Integer> counts) {
		this.counts = new EnumMap<>(Event.class);
		this.counts.putAll(counts);
		totalPoints = counts.entrySet().stream().mapToInt(e -> e.getValue() * e.getKey().getPointValue()).sum();
	}

	public static ScoreState fromScore(Score score) {
		if (score instanceof ScoreState)
			return (ScoreState) score;
		if (score instanceof DynamicScore)
			return ((DynamicScore) score).getState();
		Map<Event, Integer> counts = new EnumMap<>(Event.class);
		for (Event event : Event.values()) {
			int count = score.getCount(event);
			if (count != 0)
				counts.put(event, count);
		}
		return new ScoreState(counts);
	}

	@Override
	public int getCount(Event event) {
		return counts.getOrDefault(event, 0);
	}

	@Override
	public int getPoints(Event event) {
		return getCount(event) * event.getPointValue();
	}

	@Override
	public int getTotalPoints() {
		return totalPoints;
	}

	public ScoreState addEvent(Event event) {

		if (event.getType().equals(Event.Type.ONE_TIME) && getCount(event) >= 1)
			return this;

		Map<Event, Integer> newEventCounts = new EnumMap<>(counts);
		newEventCounts.merge(event, 1, Integer::sum);

		return new ScoreState(newEventCounts);

	}

	@Override
	public String toString() {
		return counts.toString();
	}

	public enum Serializer implements JsonSerializer<ScoreState> {
		INSTANCE;

		@Override
		public JsonElement serialize(ScoreState src, Type typeOfSrc, JsonSerializationContext context) {
			Type type = new TypeToken<Map<Event, Integer>>() {
			}.getType();
			return context.serialize(src.counts, type);
		}

	}

	public enum Deserializer implements JsonDeserializer<ScoreState> {
		INSTANCE;

		@Override
		public ScoreState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Type type = new TypeToken<Map<Event, Integer>>() {
			}.getType();
			Map<Event, Integer> counts = context.deserialize(json, type);
			return new ScoreState(counts);
		}
	}

}
