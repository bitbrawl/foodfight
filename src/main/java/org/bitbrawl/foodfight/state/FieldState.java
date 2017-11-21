package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;

import net.jcip.annotations.Immutable;

@Immutable
public final class FieldState implements Field, Serializable {

	private final int turnNumber;
	private Collection<TeamState> teams;
	private transient Collection<PlayerState> players;
	private Collection<FoodState> foods;
	private Collection<Collision> collisions;

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

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		teams = Collections.unmodifiableList(new ArrayList<>(teams));
		List<PlayerState> tempPlayers = new ArrayList<>();
		for (TeamState team : teams)
			tempPlayers.addAll(team.getPlayers());
		players = Collections.unmodifiableList(tempPlayers);
		foods = Collections.unmodifiableList(new ArrayList<>(foods));
		collisions = Collections.unmodifiableList(new ArrayList<>(collisions));

	}

	private static final long serialVersionUID = 4660387823717821917L;

}
