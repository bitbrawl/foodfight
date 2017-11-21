package org.bitbrawl.foodfight.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;

import net.jcip.annotations.Immutable;

@Immutable
public final class FieldState implements Field {

	private final int turnNumber;
	private final Collection<TeamState> teams;
	private final Collection<PlayerState> players;
	private final Collection<FoodState> foods;
	private final Collection<Collision> collisions;

	public FieldState(int turnNumber, Collection<? extends TeamState> teams, Collection<? extends FoodState> foods,
			Collection<? extends Collision> collisions) {
		this.turnNumber = turnNumber;
		this.teams = Collections.unmodifiableList(new ArrayList<>(teams));
		List<PlayerState> tempPlayers = new ArrayList<>();
		for (TeamState team : teams)
			tempPlayers.addAll(team.getPlayers());
		this.players = Collections.unmodifiableList(tempPlayers);
		this.foods = Collections.unmodifiableList(new ArrayList<>(foods));
		this.collisions = Collections.unmodifiableList(new ArrayList<>(collisions));
	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}

	@Override
	public Collection<TeamState> getTeams() {
		return teams;
	}

	@Override
	public Collection<PlayerState> getPlayers() {
		return players;
	}

	@Override
	public Collection<FoodState> getFood() {
		return foods;
	}

	@Override
	public Collection<Collision> getCollisions() {
		return collisions;
	}

}
