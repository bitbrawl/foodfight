package org.bitbrawl.foodfight.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.FieldElementState;
import org.bitbrawl.foodfight.team.Team;

public final class PlayerHook implements AutoCloseable {

	private final JavaPlayer player;
	private final ClassLoader loader;
	private final Clock clock;

	private PlayerHook(JavaPlayer player, ClassLoader loader, Clock clock) {
		this.loader = loader;
		this.clock = clock;
		if (loader != null)
			clocks.put(loader, clock);
		this.player = player;
	}

	private PlayerHook(JavaPlayer player) {
		this.player = player;
		this.loader = null;
		this.clock = null;
	}

	public static synchronized PlayerHook newPlayerHook(ClassLoader loader, String className, String versionName,
			Field field, Team team, Inventory inventory, char symbol, float hue, Logger matchLogger,
			Function<JavaPlayer, Logger> loggerFunction) throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		Clock clock = new Clock();
		JavaPlayer player;
		JavaPlayer.setStaticFields(versionName, field, team, clock, inventory, symbol, hue, loggerFunction);
		player = newPlayer(loader, className, versionName, field, team, clock, inventory, symbol, hue, matchLogger,
				loggerFunction);
		return new PlayerHook(player, loader, clock);
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

	@Override
	public void close() {
		clocks.remove(loader);
	}

	public static Clock getClock(Class<?> clazz) {
		Clock clock = clocks.get(clazz.getClassLoader());
		return clock == null ? dummyClock : clock;
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

	private static final Map<ClassLoader, Clock> clocks = new HashMap<>();
	private static final Clock dummyClock = new Clock();

}
