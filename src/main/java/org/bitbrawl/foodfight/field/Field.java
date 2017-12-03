package org.bitbrawl.foodfight.field;

import java.util.Set;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.state.FieldState;
import org.bitbrawl.foodfight.team.Team;

public interface Field {

	public int getTurnNumber();

	public Set<Team> getTeams();

	public Set<Player> getPlayers();

	public Set<FoodPiece> getFood();

	public Set<Collision> getCollisions();

	public FieldState getState();

	public static final int DEPTH = 1080;
	public static final int WIDTH = 1440;
	public static final int MAX_FOOD = 4;
	public static final int TOTAL_TURNS = 1000;
	// seconds
	public static final long TIME_LIMIT = 60;

}
