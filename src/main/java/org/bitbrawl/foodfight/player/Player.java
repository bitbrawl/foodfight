package org.bitbrawl.foodfight.player;

import java.util.concurrent.TimeUnit;

import org.bitbrawl.foodfight.field.FieldElement;
import org.bitbrawl.foodfight.random.RandomScalar;
import org.bitbrawl.foodfight.state.PlayerState;
import org.bitbrawl.foodfight.team.Team;
import org.bitbrawl.foodfight.util.Direction;

public interface Player extends FieldElement {

	public char getSymbol();

	public Team getTeam();

	public double getHeight();

	public Direction getHeading();

	public Inventory getInventory();

	public double getHealth();

	public long getTimeLeft(TimeUnit unit);

	public PlayerState getState();

	public static final double HEIGHT = 200.0;
	public static final double MIN_HEIGHT = 100.0;
	public static final RandomScalar THROW_HEIGHT = new RandomScalar(150.0, 10.0);
	public static final RandomScalar FORWARD_MOVEMENT_SPEED = new RandomScalar(10.0, 1.0);
	public static final RandomScalar REVERSE_MOVEMENT_SPEED = new RandomScalar(5.0, 1.0);
	public static final RandomScalar DUCK_SPEED = new RandomScalar(5.0, 1.0);
	public static final RandomScalar TURN_SPEED = new RandomScalar(5.0, 1.0);
	public static final double REACH_DISTANCE = 100.0;
	public static final double REACH_RANGE = 60.0;
	public static final double LEFT_ARM_DIRECTION = -30.0;
	public static final double RIGHT_ARM_DIRECTION = 30.0;
	public static final double COLLISION_RADIUS = 50.0;
	public static final double MAX_HEALTH = 100.0;

}
