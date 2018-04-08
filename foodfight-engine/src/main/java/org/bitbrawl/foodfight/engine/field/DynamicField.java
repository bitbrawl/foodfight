package org.bitbrawl.foodfight.engine.field;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicField implements Field, Dynamic<FieldState> {

	private FieldState state;
	private final Set<DynamicTeam> dynamicTeams;
	private final Set<Team> teams;
	private final Set<DynamicPlayer> dynamicPlayers;
	private final Set<Player> players;
	private final Set<DynamicFood> mutableFood = new LinkedHashSet<>();
	private final Set<DynamicFood> dynamicFood = Collections.unmodifiableSet(mutableFood);
	private final Set<Food> food = Collections.unmodifiableSet(mutableFood);

	public DynamicField(FieldState state) {

		this.state = state;

		Set<DynamicTeam> tempTeams = new LinkedHashSet<>();
		for (TeamState team : state.getTeamStates())
			tempTeams.add(new DynamicTeam(team));
		dynamicTeams = Collections.unmodifiableSet(tempTeams);
		teams = Collections.unmodifiableSet(tempTeams);

		dynamicPlayers = FieldState.playersFromTeams(tempTeams, DynamicTeam::getDynamicPlayers);
		players = Collections.unmodifiableSet(dynamicPlayers);

		for (FoodState piece : state.getFoodStates())
			mutableFood.add(new DynamicFood(piece));

	}

	@Override
	public int getTurnNumber() {
		return state.getTurnNumber();
	}

	@Override
	public MatchType getMatchType() {
		return state.getMatchType();
	}

	@Override
	public Set<Team> getTeams() {
		return teams;
	}

	public Set<DynamicTeam> getDynamicTeams() {
		return dynamicTeams;
	}

	@Override
	public DynamicTeam getTeam(char symbol) {
		for (DynamicTeam team : dynamicTeams)
			if (team.getSymbol() == symbol)
				return team;
		return null;
	}

	@Override
	public DynamicTeam getTeam(Player player) {
		for (DynamicTeam team : dynamicTeams)
			for (DynamicPlayer teamPlayer : team.getDynamicPlayers())
				if (teamPlayer.equals(player))
					return team;
		return null;
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	public Set<DynamicPlayer> getDynamicPlayers() {
		return dynamicPlayers;
	}

	@Override
	public DynamicPlayer getPlayer(char symbol) {
		for (Player player : players)
			if (player.getSymbol() == symbol)
				return (DynamicPlayer) player;
		return null;
	}

	@Override
	public Set<Food> getFood() {
		return food;
	}

	public Set<DynamicFood> getDynamicFood() {
		return dynamicFood;
	}

	@Override
	public DynamicFood getFood(Food.Type type) {
		for (DynamicFood piece : dynamicFood)
			if (piece.getType().equals(type))
				return piece;
		return null;
	}

	@Override
	public Set<Collision> getCollisions() {
		return state.getCollisions();
	}

	@Override
	public FieldState getState() {
		return state;
	}

	@Override
	public void update(FieldState state) {
		Objects.requireNonNull(state);

		this.state = state;

		for (DynamicTeam team : dynamicTeams)
			team.update(state.getTeam(team.getSymbol()));

		for (Iterator<DynamicFood> it = mutableFood.iterator(); it.hasNext();) {
			DynamicFood piece = it.next();
			FoodState updatedPiece = state.getFood(piece.getType());
			if (updatedPiece == null)
				it.remove();
			else
				piece.update(updatedPiece);
		}
		for (FoodState updatedPiece : state.getFoodStates()) {
			DynamicFood existingPiece = getFood(updatedPiece.getType());
			if (existingPiece == null)
				mutableFood.add(new DynamicFood(updatedPiece));
		}

	}

	@Override
	public String toString() {
		return "DynamicField";
	}

}
