package org.bitbrawl.foodfight.engine.field;

import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.ArrayList;
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
	private final Set<TeamState> teams;
	private final transient Set<Team> accessibleTeams;
	private final transient Set<PlayerState> players;
	private final transient Set<Player> accessiblePlayers;
	private final Set<FoodState> food;
	private final transient Set<Food> accessiblefood;
	private final Set<CollisionState> collisions;
	private final transient Set<Collision> accessibleCollisions;

	public FieldState(int turnNumber, Collection<TeamState> teams, Collection<FoodState> food,
			Collection<CollisionState> collisions) {

		this.turnNumber = turnNumber;

		Set<TeamState> tempTeams = new LinkedHashSet<>(teams);
		this.teams = Collections.unmodifiableSet(tempTeams);
		accessibleTeams = Collections.unmodifiableSet(tempTeams);

		players = playersFromTeams(tempTeams, TeamState::getPlayerStates);
		accessiblePlayers = Collections.unmodifiableSet(players);

		Set<FoodState> tempFood = new LinkedHashSet<>(food);
		this.food = Collections.unmodifiableSet(tempFood);
		accessiblefood = Collections.unmodifiableSet(tempFood);

		Set<CollisionState> tempCollisions = new LinkedHashSet<>(collisions);
		this.collisions = Collections.unmodifiableSet(tempCollisions);
		accessibleCollisions = Collections.unmodifiableSet(tempCollisions);

	}

	public static FieldState fromField(Field field) {
		if (field instanceof FieldState)
			return (FieldState) field;
		if (field instanceof DynamicField)
			return ((DynamicField) field).getState();
		Set<Team> fieldTeams = field.getTeams();
		Collection<TeamState> teams = new ArrayList<>(fieldTeams.size());
		for (Team team : fieldTeams)
			teams.add(TeamState.fromTeam(team));
		Set<Food> fieldFoods = field.getFood();
		Collection<FoodState> foods = new ArrayList<>(fieldFoods.size());
		for (Food food : field.getFood())
			foods.add(FoodState.fromFood(food));
		Set<Collision> fieldCollisions = field.getCollisions();
		Collection<CollisionState> collisions = new ArrayList<>(fieldCollisions.size());
		for (Collision collision : field.getCollisions())
			collisions.add(CollisionState.fromCollision(collision));
		return new FieldState(field.getTurnNumber(), teams, foods, collisions);
	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}

	@Override
	public Set<Team> getTeams() {
		return accessibleTeams;
	}

	public Set<TeamState> getTeamStates() {
		return teams;
	}

	@Override
	public TeamState getTeam(char symbol) {
		for (TeamState team : teams)
			if (team.getSymbol() == symbol)
				return team;
		return null;
	}

	@Override
	public TeamState getTeam(Player player) {
		for (TeamState team : teams)
			for (PlayerState teamPlayer : team.getPlayerStates())
				if (teamPlayer.equals(player))
					return team;
		return null;
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
	public Set<Food> getFood() {
		return accessiblefood;
	}

	public Set<FoodState> getFoodStates() {
		return food;
	}

	@Override
	public FoodState getFood(Food.Type type) {
		for (Food piece : accessiblefood)
			if (piece.getType() == type)
				return (FoodState) piece;
		return null;
	}

	@Override
	public Set<Collision> getCollisions() {
		return accessibleCollisions;
	}

	public Set<CollisionState> getCollisionStates() {
		return collisions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("FieldState[turnNumber=");
		result.append(turnNumber);
		result.append(",teams=");
		result.append(accessibleTeams);
		result.append(",food=");
		result.append(accessiblefood);
		result.append(",collisions=");
		result.append(accessibleCollisions);
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
			List<TeamState> teams = context.deserialize(object.get("teams"), teamsType);
			Type foodsType = new TypeToken<List<FoodState>>() {
			}.getType();
			List<FoodState> food = context.deserialize(object.get("food"), foodsType);
			Type collisionsType = new TypeToken<List<CollisionState>>() {
			}.getType();
			List<CollisionState> collisions = context.deserialize(object.get("collisions"), collisionsType);

			return new FieldState(turnNumber, teams, food, collisions);

		}

	}

}
