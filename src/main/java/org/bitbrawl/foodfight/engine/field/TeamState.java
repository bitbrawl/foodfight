package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public final class TeamState implements Team {

	private final char symbol;
	private final Set<PlayerState> playerStates;
	private final transient Set<Player> players;
	private final TableState table;
	private final ScoreState score;

	public TeamState(char symbol, Collection<? extends PlayerState> players, TableState table, ScoreState score) {
		this.symbol = symbol;
		Set<PlayerState> tempPlayers = new LinkedHashSet<>(players);
		playerStates = Collections.unmodifiableSet(tempPlayers);
		this.players = Collections.unmodifiableSet(tempPlayers);
		this.table = table;
		this.score = score;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	public Set<PlayerState> getPlayerStates() {
		return playerStates;
	}

	@Override
	public PlayerState getPlayer(char symbol) {
		for (PlayerState player : playerStates)
			if (player.getSymbol() == symbol)
				return player;
		return null;
	}

	@Override
	public TableState getTable() {
		return table;
	}

	@Override
	public ScoreState getScore() {
		return score;
	}

	public enum Deserializer implements JsonDeserializer<TeamState> {

		INSTANCE;

		@Override
		public TeamState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			char symbol = object.getAsJsonPrimitive("symbol").getAsCharacter();
			Type teamsType = new TypeToken<List<PlayerState>>() {
			}.getType();
			List<PlayerState> players = context.deserialize(object.getAsJsonArray("playerStates"), teamsType);
			TableState table = context.deserialize(object.getAsJsonObject("table"), TableState.class);
			ScoreState state = context.deserialize(object.getAsJsonObject("score"), ScoreState.class);

			return new TeamState(symbol, players, table, state);

		}

	}

}
