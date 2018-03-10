package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.jcip.annotations.Immutable;

@Immutable
public final class FieldState implements Field {

	private final int turnNumber;
	private final Set<TeamState> teamStates;
	private final transient Set<Team> teams;
	private final transient Set<PlayerState> playerStates;
	private final transient Set<Player> players;
	private final Set<FoodState> foodStates;
	private final transient Set<Food> food;
	private final Set<CollisionState> collisionStates;
	private final transient Set<Collision> collisions;

	public FieldState(int turnNumber, Collection<? extends TeamState> teams, Collection<? extends FoodState> food,
			Collection<? extends CollisionState> collisions) {

		this.turnNumber = turnNumber;

		Set<TeamState> tempTeams = new LinkedHashSet<>(teams);
		this.teamStates = Collections.unmodifiableSet(tempTeams);
		this.teams = Collections.unmodifiableSet(tempTeams);

		playerStates = playersFromTeams(tempTeams, TeamState::getPlayerStates);
		players = Collections.unmodifiableSet(playerStates);

		Set<FoodState> tempFood = new LinkedHashSet<>(food);
		foodStates = Collections.unmodifiableSet(tempFood);
		this.food = Collections.unmodifiableSet(tempFood);

		Set<CollisionState> tempCollisions = new LinkedHashSet<>(collisions);
		collisionStates = Collections.unmodifiableSet(tempCollisions);
		this.collisions = Collections.unmodifiableSet(tempCollisions);

	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}

	@Override
	public Set<Team> getTeams() {
		return teams;
	}

	public Set<TeamState> getTeamStates() {
		return teamStates;
	}

	@Override
	public TeamState getTeam(char symbol) {
		for (TeamState team : teamStates)
			if (team.getSymbol() == symbol)
				return team;
		return null;
	}

	@Override
	public TeamState getTeam(Player player) {
		for (TeamState team : teamStates)
			for (PlayerState teamPlayer : team.getPlayerStates())
				if (teamPlayer.equals(player))
					return team;
		return null;
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
	public Set<Food> getFood() {
		return food;
	}

	public Set<FoodState> getFoodStates() {
		return foodStates;
	}

	@Override
	public FoodState getFood(Food.Type type) {
		for (Food piece : food)
			if (piece.getType() == type)
				return (FoodState) piece;
		return null;
	}

	@Override
	public Set<Collision> getCollisions() {
		return collisions;
	}

	public Set<CollisionState> getCollisionStates() {
		return collisionStates;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("FieldState[turnNumber=");
		result.append(turnNumber);
		result.append(",teams=");
		result.append(teams);
		result.append(",food=");
		result.append(food);
		result.append(",collisions=");
		result.append(collisions);
		result.append(']');
		return result.toString();
	}

	static <T extends Team, P extends Player> Set<P> playersFromTeams(Collection<T> teams,
			Function<? super T, ? extends Collection<P>> function) {

		return new AbstractSet<P>() {

			@Override
			public Iterator<P> iterator() {
				return new Iterator<P>() {

					private final Iterator<T> teamIt = teams.iterator();
					private Iterator<P> playerIt = function.apply(teamIt.next()).iterator();

					@Override
					public boolean hasNext() {
						if (playerIt.hasNext())
							return true;
						if (!teamIt.hasNext())
							return false;
						playerIt = function.apply(teamIt.next()).iterator();
						return playerIt.hasNext();
					}

					@Override
					public P next() {
						if (playerIt.hasNext())
							return playerIt.next();
						playerIt = function.apply(teamIt.next()).iterator();
						return playerIt.next();
					}

				};
			}

			@Override
			public int size() {
				return teams.stream().mapToInt(team -> function.apply(team).size()).sum();
			}

		};

	}

	public enum Deserializer implements JsonDeserializer<FieldState> {

		INSTANCE;

		@Override
		public FieldState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			int turnNumber = object.getAsJsonPrimitive("turnNumber").getAsInt();
			Type teamsType = new TypeToken<List<TeamState>>() {
			}.getType();
			List<TeamState> teams = context.deserialize(object.getAsJsonArray("teamStates"), teamsType);
			Type foodsType = new TypeToken<List<FoodState>>() {
			}.getType();
			List<FoodState> food = context.deserialize(object.getAsJsonArray("foodStates"), foodsType);
			Type collisionsType = new TypeToken<List<CollisionState>>() {
			}.getType();
			List<CollisionState> collisions = context.deserialize(object.getAsJsonArray("collisionStates"),
					collisionsType);

			return new FieldState(turnNumber, teams, food, collisions);

		}

	}

}
