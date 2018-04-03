package org.bitbrawl.foodfight.engine.config;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public enum PathDeserializer implements JsonDeserializer<Path> {
	INSTANCE;

	@Override
	public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return Paths.get(json.getAsString());
	}

}
