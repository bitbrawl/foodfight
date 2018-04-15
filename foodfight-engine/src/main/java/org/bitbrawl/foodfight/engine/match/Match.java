package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.engine.field.DynamicField;
import org.bitbrawl.foodfight.engine.field.DynamicPlayer;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.field.Field;

public final class Match {

	private final int number;
	private final Map<Controller, DynamicPlayer> controllers = new LinkedHashMap<>();
	private final DynamicField field;
	private final List<FieldState> fieldStates = new ArrayList<>(Field.TOTAL_TURNS + 1);
	private final TurnRunner turnRunner;
	private final Consumer<FieldState> uiConsumer;

	public final static class Builder {

		private final int number;
		private final FieldState field;
		private final CharFunction<? extends Controller> controllers;
		private final TurnRunner turnRunner;
		private Consumer<FieldState> uiConsumer = f -> {
		};

		public Builder(int number, FieldState field, CharFunction<? extends Controller> controllers,
				TurnRunner turnRunner) {
			this.number = number;
			this.field = field;
			this.controllers = controllers;
			this.turnRunner = turnRunner;
		}

		public Builder uiConsumer(Consumer<FieldState> val) {
			this.uiConsumer = val;
			return this;
		}

		public Match build() {
			return new Match(this);
		}

	}

	private Match(Builder builder) {

		number = builder.number;
		field = new DynamicField(builder.field);

		for (DynamicPlayer player : field.getDynamicPlayers())
			controllers.put(builder.controllers.apply(player.getSymbol()), player);

		turnRunner = builder.turnRunner;

		uiConsumer = builder.uiConsumer.andThen(fieldStates::add);

		field.update(new FieldState(field.getTurnNumber() + 1, field.getMatchType(), builder.field.getTeamStates(),
				builder.field.getFoodStates(), builder.field.getCollisionStates()));

	}

	public MatchHistory run() {

		for (int turnNumber = 1; turnNumber <= Field.TOTAL_TURNS; turnNumber++) {

			addFrame();

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

		addFrame();
		return new MatchHistory(number, fieldStates);

	}

	public int getNumber() {
		return number;
	}

	private void addFrame() {
		uiConsumer.accept(field.getState());
	}

	public static String getMatchName(int matchNumber) {
		return String.format("match-%06x", matchNumber);
	}

}
