package org.bitbrawl.foodfight.field;

import java.util.Collection;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.team.Team;

public interface Field {

	public int getTurnNumber();

	public Collection<? extends Team> getTeams();

	public Collection<? extends Player> getPlayers();

	public Collection<? extends FoodPiece> getFood();

	public Collection<? extends Collision> getCollisions();

	public static final int HEIGHT = 1080;
	public static final int WIDTH = 1440;
	public static final int MAX_FOOD = 4;
	public static final int TOTAL_TURNS = 1000;

}
