package org.bitbrawl.foodfight.engine.match;

import java.util.Map;

import org.bitbrawl.foodfight.controller.Controller.Action;
import org.bitbrawl.foodfight.engine.field.FieldState;

public interface TurnRunner {

	public FieldState runTurn(FieldState field, Map<? extends Character, ? extends Action> actions);

}
