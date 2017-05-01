package org.bitbrawl.foodfight.player;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.FieldElementState;
import org.bitbrawl.foodfight.field.FoodPiece;
import org.bitbrawl.foodfight.team.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public abstract class JavaPlayer implements Player {

	private final Logger logger;

	private final String versionName;
	private final Field field;
	private final Team team;
	private final Clock clock;
	private final char symbol;
	private final float color;

	private FieldElementState state;
	private Inventory inventory;
	private double health;

	private static String staticVersionName;
	private static Field staticField;
	private static Team staticTeam;
	private static Clock staticClock;
	private static Inventory staticInventory;
	private static char staticSymbol;
	private static float staticHue;
	private static Function<JavaPlayer, Logger> staticLoggerFunction;

	static void setStaticFields(String versionName, Field field, Team team, Clock clock, Inventory inventory,
			char symbol, float hue, Function<JavaPlayer, Logger> loggerFunction) {
		staticVersionName = versionName;
		staticField = field;
		staticTeam = team;
		staticClock = clock;
		staticInventory = inventory;
		staticSymbol = symbol;
		staticHue = hue;
		staticLoggerFunction = loggerFunction;
	}

	protected JavaPlayer() {
		this(staticVersionName, staticField, staticTeam, staticClock, staticInventory, staticSymbol, staticHue,
				staticLoggerFunction);
	}

	private JavaPlayer(String versionName, Field field, Team team, Clock clock, Inventory inventory, char symbol,
			float hue, Function<JavaPlayer, Logger> loggerFunction) {

		this.versionName = versionName;
		this.field = field;
		this.team = team;
		this.clock = clock;
		this.inventory = inventory;
		this.symbol = symbol;
		this.color = hue;

		this.logger = loggerFunction.apply(this);
		// TODO add to constructor
		Vector randomPosition = Vector.cartesian(ThreadLocalRandom.current().nextDouble() * Field.WIDTH,
				ThreadLocalRandom.current().nextDouble() * Field.HEIGHT);
		state = new FieldElementState(randomPosition, HEIGHT, new Direction());
	}

	protected abstract Action playTurn(int turnNumber);

	@Override
	public final char getSymbol() {
		return symbol;
	}

	@Override
	public final float getColor() {
		return color;
	}

	@Override
	public final String toString() {
		return String.format("%c%c-%s", team.getSymbol(), symbol, versionName);
	}

	@Override
	public final boolean canPlay(Action action) {
		if (action == null)
			return true;

		switch (action) {
		case MOVE_FORWARD:
			if (state.getLocation().getX() <= 0 && state.getHeading().angle(Direction.WEST) <= 90.0)
				return false;
			if (state.getLocation().getX() >= Field.WIDTH && state.getHeading().angle(Direction.EAST) <= 90.0)
				return false;
			if (state.getLocation().getY() <= 0 && state.getHeading().angle(Direction.NORTH) <= 90.0)
				return false;
			if (state.getLocation().getY() >= Field.HEIGHT && state.getHeading().angle(Direction.SOUTH) <= 90.0)
				return false;
			return true;
		case MOVE_BACKWARD:
			if (state.getLocation().getX() <= 0 && state.getHeading().angle(Direction.EAST) <= 90.0)
				return false;
			if (state.getLocation().getX() >= Field.WIDTH && state.getHeading().angle(Direction.WEST) <= 90.0)
				return false;
			if (state.getLocation().getY() <= 0 && state.getHeading().angle(Direction.SOUTH) <= 90.0)
				return false;
			if (state.getLocation().getY() >= Field.HEIGHT && state.getHeading().angle(Direction.NORTH) <= 90.0)
				return false;
			return true;
		case TURN_LEFT:
		case TURN_RIGHT:
			return true;
		case DUCK:
			return true;
		case PICKUP_LEFT:
			if (inventory.get(Hand.LEFT) != null)
				return false;
			for (FoodPiece foodPiece : field.getFood())
				if (canPickup(foodPiece, Hand.LEFT))
					return true;
			return false;
		case PICKUP_RIGHT:
			if (inventory.get(Hand.RIGHT) != null)
				return false;
			for (FoodPiece foodPiece : field.getFood())
				if (canPickup(foodPiece, Hand.RIGHT))
					return true;
			return false;
		case THROW_LEFT:
		case EAT_LEFT:
			if (inventory.get(Hand.LEFT) == null)
				return false;
			return true;
		case THROW_RIGHT:
		case EAT_RIGHT:
			if (inventory.get(Hand.RIGHT) == null)
				return false;
			return true;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public final boolean canPickup(FoodPiece food, Hand hand) {
		Objects.requireNonNull(food, "food cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		FieldElementState foodState = food.getState();

		if (foodState.getHeight() > 0.0)
			return false;

		Vector foodLocation = food.getState().getLocation();

		double deltaX = foodLocation.getX() - state.getLocation().getX();
		double deltaY = foodLocation.getY() - state.getLocation().getY();
		double distanceSquared = deltaX * deltaX + deltaY * deltaY;
		if (distanceSquared > REACH_DISTANCE * REACH_DISTANCE)
			return false;

		Direction toFood = new Direction(Vector.cartesian(deltaX, deltaY));
		return getArmDirection(hand).angle(toFood) < REACH_RANGE / 2.0;

	}

	protected final Field getField() {
		return field;
	}

	@Override
	public final Team getTeam() {
		return team;
	}

	protected final Clock getClock() {
		return clock;
	}

	protected final Logger getLogger() {
		return logger;
	}

	@Override
	public final Direction getArmDirection(Hand hand) {
		if (hand == null)
			throw new NullPointerException();

		switch (hand) {
		case LEFT:
			return state.getHeading().add(LEFT_ARM_DIRECTION);
		case RIGHT:
			return state.getHeading().add(RIGHT_ARM_DIRECTION);
		default:
			throw new AssertionError();
		}

	}

	@Override
	public final Vector getArmLocation(Hand hand) {
		if (hand == null)
			throw new NullPointerException();

		return state.getLocation().add(Vector.polar(REACH_DISTANCE, getArmDirection(hand)));

	}

	@Override
	public final FieldElementState getState() {
		return state;
	}

	final void setState(FieldElementState state) {
		this.state = state;
	}

	@Override
	public final double getRadius() {
		return COLLISION_RADIUS;
	}

	@Override
	public final Inventory getInventory() {
		return inventory;
	}

	final void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public final double getHealth() {
		return health;
	}

	final void addHealth(double health) {
		this.health += health;
		if (health > MAX_HEALTH)
			this.health = MAX_HEALTH;
	}

	final void decrementHealth() {
		health -= HEALTH_DROP_RATE;
	}

}
