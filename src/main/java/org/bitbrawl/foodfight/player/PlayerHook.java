package org.bitbrawl.foodfight.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.FieldElementState;
import org.bitbrawl.foodfight.team.Team;

public final class PlayerHook {

	private final JavaPlayer player;
	private final Clock clock;

	private PlayerHook(JavaPlayer player, Clock clock) {
		this.clock = clock;
		this.player = player;
	}

	private PlayerHook(JavaPlayer player) {
		this.player = player;
		this.clock = new Clock();
	}

	public static PlayerHook newPlayerHook(ClassLoader loader, String className, String versionName, Field field,
			Team team, Inventory inventory, char symbol, float hue, Logger matchLogger,
			Function<JavaPlayer, Logger> loggerFunction) throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		Clock clock = new Clock();
		JavaPlayer player;
		synchronized (JavaPlayer.class) {
			JavaPlayer.setStaticFields(versionName, field, team, clock, inventory, symbol, hue, loggerFunction);
			player = newPlayer(loader, className, versionName, field, team, clock, inventory, symbol, hue, matchLogger,
					loggerFunction);
		}
		return new PlayerHook(player, clock);
	}

	public static PlayerHook newDummyHook(String versionName, Field field, Team team, char symbol, float hue) {
		JavaPlayer dummy = newDummy(versionName, field, team, symbol, hue);
		return new PlayerHook(dummy);
	}

	public JavaPlayer getPlayer() {
		return player;
	}

	public Action playTurn(int turnNumber) {
		return player.playTurn(turnNumber);
	}

	public Clock getClock() {
		return clock;
	}

	public void startTurn(int turnNumber) {
		clock.startTurn(turnNumber);
	}

	public void endTurn() {
		clock.endTurn();
	}

	public boolean isOutOfTime() {
		return clock.isOutOfTime();
	}

	public void setState(FieldElementState state) {
		player.setState(state);
	}

	public void setInventory(Inventory inventory) {
		player.setInventory(inventory);
	}

	public void addHealth(double health) {
		player.addHealth(health);
	}

	public void decrementHealth() {
		player.decrementHealth();
	}

	private static JavaPlayer newPlayer(ClassLoader loader, String className, String name, Field field, Team team,
			Clock clock, Inventory inventory, char symbol, float hue, Logger matchLogger,
			Function<JavaPlayer, Logger> loggerFunction) throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {

		Class<?> playerClass = loader.loadClass(className);
		Class<? extends JavaPlayer> subclass = playerClass.asSubclass(JavaPlayer.class);
		Constructor<? extends JavaPlayer> constructor = subclass.getConstructor();

		return constructor.newInstance();

	}

	public static JavaPlayer newDummy(String versionName, Field field, Team team, char symbol, float hue) {

		return new JavaPlayer() {
			@Override
			public Action playTurn(int turnNumber) {
				return null;
			}
		};

	}

}
