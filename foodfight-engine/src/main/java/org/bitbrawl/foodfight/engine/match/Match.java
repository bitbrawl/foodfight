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
import java.util.logging.Level;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.engine.field.DynamicField;
import org.bitbrawl.foodfight.engine.field.DynamicPlayer;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.field.Field;

public final class Match {

	private final int number;
	private final Map<Controller, DynamicPlayer> controllers = new LinkedHashMap<>();
	private final DynamicField field;
	private final List<FieldState> fieldStates = new ArrayList<>(Field.TOTAL_TURNS + 1);
	private final TurnRunner turnRunner;
	private final Consumer<FieldState> videoConsumer;

	public Match(int number, FieldState field, CharFunction<? extends Controller> controllers,
			TurnRunner turnRunner, Consumer<FieldState> videoConsumer) {
		Objects.requireNonNull(field, "field cannot be null");
		Objects.requireNonNull(controllers, "controllers cannot be null");

		this.number = number;
		this.field = new DynamicField(field);
		this.videoConsumer = videoConsumer.andThen(fieldStates::add);

		for (DynamicPlayer player : this.field.getDynamicPlayers())
			this.controllers.put(controllers.apply(player.getSymbol()), player);

		this.turnRunner = turnRunner;

		this.field.update(new FieldState(field.getTurnNumber() + 1, field.getMatchType(), field.getTeamStates(),
				field.getFoodStates(), field.getCollisionStates()));

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
		try {
			videoConsumer.accept(field.getState());
		} catch (Throwable t) {
			EngineLogger.INSTANCE.log(Level.WARNING, "Unable to add video frame", t);
		}
	}

}
