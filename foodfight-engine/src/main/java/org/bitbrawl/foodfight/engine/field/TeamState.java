package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
	private final Set<PlayerState> players;
	private final transient Set<Player> accessiblePlayers;
	private final TableState table;
	private final ScoreState score;

	public TeamState(char symbol, Collection<PlayerState> players, TableState table, ScoreState score) {
		this.symbol = symbol;
		Set<PlayerState> tempPlayers = new LinkedHashSet<>(players);
		this.players = Collections.unmodifiableSet(tempPlayers);
		accessiblePlayers = Collections.unmodifiableSet(tempPlayers);
		this.table = table;
		this.score = score;
	}

	public static TeamState fromTeam(Team team) {
		if (team instanceof TeamState)
			return (TeamState) team;
		if (team instanceof DynamicTeam)
			return ((DynamicTeam) team).getState();
		Set<Player> teamPlayers = team.getPlayers();
		Collection<PlayerState> players = new ArrayList<>(teamPlayers.size());
		for (Player player : teamPlayers)
			players.add(PlayerState.fromPlayer(player));
		TableState table = TableState.fromTable(team.getTable());
		ScoreState score = ScoreState.fromScore(team.getScore());
		return new TeamState(team.getSymbol(), players, table, score);
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public Set<Player> getPlayers() {
		return accessiblePlayers;
	}

	public Set<PlayerState> getPlayerStates() {
		return players;
	}

	@Override
	public PlayerState getPlayer(char symbol) {
		for (PlayerState player : players)
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

	@Override
	public String toString() {
		return "TeamState[symbol=" + symbol + ",players=" + accessiblePlayers + ",table=" + table + ",score=" + score
				+ ']';
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
			List<PlayerState> players = context.deserialize(object.get("players"), teamsType);
			TableState table = context.deserialize(object.get("table"), TableState.class);
			ScoreState state = context.deserialize(object.get("score"), ScoreState.class);

			return new TeamState(symbol, players, table, state);

		}

	}

}
