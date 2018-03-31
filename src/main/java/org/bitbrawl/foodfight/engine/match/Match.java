package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.engine.field.DynamicField;
import org.bitbrawl.foodfight.engine.field.DynamicPlayer;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.field.Field;

public final class Match {

	private final Map<Controller, DynamicPlayer> controllers = new LinkedHashMap<>();
	private final DynamicField field;
	private final List<FieldState> fieldStates = new ArrayList<>(Field.TOTAL_TURNS + 1);
	private final TurnRunner turnRunner;
	private final Consumer<FieldState> videoConsumer;

	public Match(FieldState field, Function<Character, ? extends Controller> controllers, TurnRunner turnRunner,
			Consumer<FieldState> videoConsumer) {
		Objects.requireNonNull(field, "field cannot be null");
		Objects.requireNonNull(controllers, "controllers cannot be null");

		this.field = new DynamicField(field);
		this.videoConsumer = videoConsumer.andThen(fieldStates::add);

		for (DynamicPlayer player : this.field.getDynamicPlayers())
			this.controllers.put(controllers.apply(player.getSymbol()), player);

		this.turnRunner = turnRunner;

		this.field.update(new FieldState(field.getTurnNumber() + 1, field.getTeamStates(), field.getFoodStates(),
				field.getCollisionStates()));

	}

	public MatchHistory run() {

		for (int turnNumber = 1; turnNumber <= Field.TOTAL_TURNS; turnNumber++) {

			videoConsumer.accept(field.getState());

			List<Entry<Controller, DynamicPlayer>> controllerOrder = new ArrayList<>(controllers.entrySet());
			Collections.shuffle(controllerOrder, ThreadLocalRandom.current());
			Map<Character, Action> actions = new LinkedHashMap<>();

			for (Entry<Controller, DynamicPlayer> entry : controllerOrder) {
				Controller controller = entry.getKey();
				DynamicPlayer player = entry.getValue();

				actions.put(player.getSymbol(), controller.playAction(field, field.getTeam(player), player));

			}

			field.update(turnRunner.runTurn(field.getState(), actions));

		}

		videoConsumer.accept(field.getState());
		return new MatchHistory(fieldStates);

	}

	public enum Type {
		DUEL("duel", 2, 2), FREE_FOR_ALL("free-for-all", 3, 3), TEAM("team", 2, 4);

		private final String name;
		private final int numberOfTeams;
		private final int numberOfPlayers;

		private Type(String name, int numberOfTeams, int numberOfPlayers) {
			this.name = name;
			this.numberOfTeams = numberOfTeams;
			this.numberOfPlayers = numberOfPlayers;
		}

		@Override
		public String toString() {
			return name;
		}

		public int getNumberOfTeams() {
			return numberOfTeams;
		}

		public int getNumberOfPlayers() {
			return numberOfPlayers;
		}

		public static Type byNumberOfPlayers(int numPlayers) {
			switch (numPlayers) {
			case 2:
				return DUEL;
			case 3:
				return FREE_FOR_ALL;
			case 4:
				return TEAM;
			default:
				throw new IllegalArgumentException(Integer.toString(numPlayers));
			}
		}

	}

}
