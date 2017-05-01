package org.bitbrawl.foodfight.player;

import org.bitbrawl.foodfight.field.FieldElement;
import org.bitbrawl.foodfight.field.FoodPiece;
import org.bitbrawl.foodfight.team.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public interface Player extends FieldElement {

	public char getSymbol();

	public float getColor();

	public Team getTeam();

	public Inventory getInventory();

	public double getHealth();

	public boolean canPlay(Action action);

	public boolean canPickup(FoodPiece food, Hand hand);

	public Direction getArmDirection(Hand hand);

	public Vector getArmLocation(Hand hand);

	public static final double HEIGHT = 200.0;
	public static final double MIN_HEIGHT = 100.0;
	public static final double FORWARD_MOVEMENT_SPEED = 10.0;
	public static final double REVERSE_MOVEMENT_SPEED = 5.0;
	public static final double DUCKED_FORWARD_MOVEMENT_SPEED = 5.0;
	public static final double DUCKED_REVERSE_MOVEMENT_SPEED = 2.0;
	public static final double DUCK_SPEED = 5.0;
	public static final double STAND_SPEED = 2.0;
	public static final double TURN_SPEED = 5.0;
	public static final double REACH_DISTANCE = 100.0;
	public static final double REACH_RANGE = 60.0;
	public static final double LEFT_ARM_DIRECTION = -30.0;
	public static final double RIGHT_ARM_DIRECTION = 30.0;
	public static final double COLLISION_RADIUS = 50.0;
	public static final double MAX_HEALTH = 100.0;
	public static final double HEALTH_DROP_RATE = 0.001;

}
