package org.bitbrawl.foodfight.engine.config;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public enum PathSerializer implements JsonSerializer<Path> {
	INSTANCE;

	@Override
	public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
