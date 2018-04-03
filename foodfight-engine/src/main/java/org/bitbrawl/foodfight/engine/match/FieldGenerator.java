package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.FoodState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.PlayerState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.field.Event;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Food;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Player.Hand;
import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public final class FieldGenerator implements Supplier<FieldState> {

	private final Match.Type type;

	public FieldGenerator(Match.Type type) {
		this.type = type;
	}

	@Override
	public FieldState get() {

		Queue<FieldZone> zones = getFieldZones();

		return new FieldState(0, generateTeams(zones), generateFood(zones), Collections.emptySet());

	}

	private static Queue<FieldZone> getFieldZones() {

		List<FieldZone> result = new ArrayList<>(8);

		double oneThirdWidth = Field.WIDTH / 3.0;
		double twoThirdsWidth = 2 * Field.WIDTH / 3.0;
		double oneThirdDepth = Field.DEPTH / 3.0;
		double twoThirdsDepth = 2 * Field.DEPTH / 3.0;

		result.add(new FieldZone(0.0, oneThirdWidth, 0.0, oneThirdDepth));
		result.add(new FieldZone(0.0, oneThirdWidth, oneThirdDepth, twoThirdsDepth));
		result.add(new FieldZone(0.0, oneThirdWidth, twoThirdsDepth, Field.DEPTH));
		result.add(new FieldZone(oneThirdWidth, twoThirdsWidth, 0.0, oneThirdDepth));
		result.add(new FieldZone(oneThirdWidth, twoThirdsWidth, twoThirdsDepth, Field.DEPTH));
		result.add(new FieldZone(twoThirdsWidth, Field.WIDTH, 0.0, oneThirdDepth));
		result.add(new FieldZone(twoThirdsWidth, Field.WIDTH, oneThirdDepth, twoThirdsDepth));
		result.add(new FieldZone(twoThirdsWidth, Field.WIDTH, twoThirdsDepth, Field.DEPTH));

		Collections.shuffle(result);

		return new LinkedList<>(result);

	}

	private Collection<TeamState> generateTeams(Queue<? extends FieldZone> zones) {

		int numTeams = type.getNumberOfTeams();
		int playersPerTeam = type.getNumberOfPlayers() / numTeams;

		TeamState[] result = new TeamState[numTeams];

		List<Character> teamSymbols = new LinkedList<>(ALPHABET);
		List<Character> playerSymbols = new LinkedList<>(DIGITS);

		for (int i = 0; i < type.getNumberOfTeams(); i++) {

			char teamSymbol = removeRandomElementFrom(teamSymbols);
			List<PlayerState> players = generatePlayers(playersPerTeam, playerSymbols, zones);
			TableState table = new TableState(randomLocationInZone(zones.remove(), Table.RADIUS),
					EnumSet.noneOf(Food.Type.class));

			result[i] = new TeamState(teamSymbol, players, table, new ScoreState(new EnumMap<>(Event.class)));

		}

		return Arrays.asList(result);

	}

	private Collection<FoodState> generateFood(Queue<? extends FieldZone> zones) {

		List<FieldZone> foodZones = new ArrayList<>(Field.MAX_FOOD);
		if (type.equals(Match.Type.DUEL))
			for (int i = 0; i < Field.MAX_FOOD; i++)
				foodZones.add(zones.remove());
		else
			for (int i = 0; i < Field.MAX_FOOD / 2; i++)
				foodZones.addAll(zones.remove().split());

		List<Food.Type> types = new ArrayList<>(Arrays.asList(Food.Type.values()));
		Collections.shuffle(types);

		FoodState[] food = new FoodState[Field.MAX_FOOD];
		for (int i = 0; i < Field.MAX_FOOD; i++) {
			Food.Type foodType = removeRandomElementFrom(types);
			food[i] = new FoodState(foodType, randomLocationInZone(foodZones.get(i), foodType.getRadius()), 0.0,
					Direction.NORTH);
		}

		List<FoodState> result = Arrays.asList(food);
		Collections.shuffle(result, ThreadLocalRandom.current());
		return result;

	}

	private static List<PlayerState> generatePlayers(int numPlayers, List<? extends Character> playerSymbols,
			Queue<? extends FieldZone> zones) {

		PlayerState[] result = new PlayerState[numPlayers];

		for (int i = 0; i < numPlayers; i++) {

			char symbol = removeRandomElementFrom(playerSymbols);
			Vector location = randomLocationInZone(zones.remove(), Player.COLLISION_RADIUS);
			Direction heading = Direction.random();
			InventoryState inventory = new InventoryState(new EnumMap<>(Hand.class));

			result[i] = new PlayerState(symbol, location, 0.0, heading, inventory, Player.MAX_HEALTH);

		}

		return Arrays.asList(result);

	}

	private static Vector randomLocationInZone(FieldZone zone, double radius) {

		double x = ThreadLocalRandom.current().nextDouble(zone.getXMin() + radius, zone.getXMax() - radius);
		double y = ThreadLocalRandom.current().nextDouble(zone.getYMin() + radius, zone.getYMax() - radius);
		return Vector.cartesian(x, y);

	}

	private static <E> E removeRandomElementFrom(List<? extends E> list) {
		return list.remove(ThreadLocalRandom.current().nextInt(list.size()));
	}

	private static final List<Character> ALPHABET;
	private static final List<Character> DIGITS;
	static {

		Character[] alphabet = new Character[26];
		int i = 0;
		for (char letter = 'A'; letter <= 'Z'; letter++)
			alphabet[i++] = letter;
		ALPHABET = Collections.unmodifiableList(Arrays.asList(alphabet));

		Character[] digits = new Character[10];
		i = 0;
		for (char digit = '0'; digit <= '9'; digit++)
			digits[i++] = digit;
		DIGITS = Collections.unmodifiableList(Arrays.asList(digits));

	}

}
