package org.bitbrawl.foodfight.field;

import java.util.Collection;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.team.Team;

public interface Field {

	public int getTurnNumber();

	public Collection<Team> getTeams();

	public Collection<Player> getPlayers();

	public Collection<FoodPiece> getFood();

	public Collection<Collision> getCollisions();

	public static final int DEPTH = 1080;
	public static final int WIDTH = 1440;
	public static final int MAX_FOOD = 4;
	public static final int TOTAL_TURNS = 1000;

}
