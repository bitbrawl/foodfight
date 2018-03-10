package org.bitbrawl.foodfight.engine.field;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

public final class DynamicTeam implements Team, Dynamic<TeamState> {

	private TeamState state;
	private final Set<DynamicPlayer> dynamicPlayers;
	private final Set<Player> players;
	private final DynamicTable table;
	private final DynamicScore score;

	public DynamicTeam(TeamState state) {
		this.state = state;
		Set<DynamicPlayer> tempPlayers = new LinkedHashSet<>();
		for (PlayerState player : state.getPlayerStates())
			tempPlayers.add(new DynamicPlayer(player));
		dynamicPlayers = Collections.unmodifiableSet(tempPlayers);
		players = Collections.unmodifiableSet(tempPlayers);
		table = new DynamicTable(state.getTable());
		score = new DynamicScore(state.getScore());
	}

	@Override
	public char getSymbol() {
		return state.getSymbol();
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
		for (DynamicPlayer player : dynamicPlayers)
			if (player.getSymbol() == symbol)
				return player;
		return null;
	}

	@Override
	public DynamicTable getTable() {
		return table;
	}

	@Override
	public DynamicScore getScore() {
		return score;
	}

	@Override
	public TeamState getState() {
		return state;
	}

	@Override
	public void update(TeamState state) {
		Objects.requireNonNull(state, "state cannot be null");
		if (this.state.getSymbol() != state.getSymbol())
			throw new IllegalArgumentException("This team's symbol cannot change");
		this.state = state;
		for (DynamicPlayer player : dynamicPlayers)
			player.update(state.getPlayer(player.getSymbol()));
		table.update(state.getTable());
		score.update(state.getScore());
	}

}
