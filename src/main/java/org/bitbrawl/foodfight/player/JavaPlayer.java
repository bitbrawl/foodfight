package org.bitbrawl.foodfight.player;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.state.PlayerState;
import org.bitbrawl.foodfight.team.Team;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class JavaPlayer extends ActivePlayer {

	private final Logger logger;
	private final Field field;
	private final Clock clock = new Clock(Field.TIME_LIMIT, TimeUnit.MINUTES);

	private final Lock turnLock = new ReentrantLock();

	protected JavaPlayer() {
		super(teamCopy.get(), playerCopy.get());
		assert teamCopy.get() != null;
		assert playerCopy.get() != null;
		logger = loggerCopy.get();
		assert logger != null;
		field = fieldCopy.get();
		assert field != null;
	}

	protected final Logger getLogger() {
		return logger;
	}

	protected final Field getField() {
		return field;
	}

	@Override
	public long getTimeLeft(TimeUnit unit) {
		return clock.timeLeft(unit);
	}

	Action playTurnInternal() throws InterruptedException, ExecutionException, TimeoutException {
		if (!turnLock.tryLock())
			throw new IllegalStateException("Another turn is already in progress");
		try {
			ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
					ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
			long timeLeftSeconds = clock.timeLeft(TimeUnit.SECONDS);
			try {
				Action result = pool.submit(() -> {
					assert pool.getActiveThreadCount() == 1;
					clock.start();
					Action action = playTurn();
					if (pool.getActiveThreadCount() == 1)
						clock.endIfRunning();
					return action;
				}).get(timeLeftSeconds + 2L, TimeUnit.SECONDS);
				timeLeftSeconds = clock.timeLeft(TimeUnit.SECONDS);
				pool.shutdownNow();
				if (!pool.awaitTermination(timeLeftSeconds + 2L, TimeUnit.SECONDS))
					throw new TimeoutException("Took too long for the pool to shut down");
				clock.endIfRunning();
				if (clock.timeLeft(TimeUnit.NANOSECONDS) < 0)
					throw new TimeoutException("Ran out of time");
				return result;
			} finally {
				clock.endIfRunning();
			}
		} finally {
			turnLock.unlock();
		}
	}

	static JavaPlayer newPlayer(PlayerState playerCopy, Class<? extends JavaPlayer> clazz, Logger logger, Field field)
			throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		assert playerCopy == null;
		assert logger == null;
		assert field == null;

		try {
			JavaPlayer.playerCopy.set(playerCopy);
			JavaPlayer.loggerCopy.set(logger);
			JavaPlayer.fieldCopy.set(field);
			return clazz.getConstructor().newInstance();
		} finally {
			JavaPlayer.playerCopy.set(null);
			JavaPlayer.loggerCopy.set(null);
			JavaPlayer.fieldCopy.set(null);
		}

	}

	private static final ThreadLocal<Team> teamCopy = new ThreadLocal<>();
	private static final ThreadLocal<PlayerState> playerCopy = new ThreadLocal<>();
	private static final ThreadLocal<Logger> loggerCopy = new ThreadLocal<>();
	private static final ThreadLocal<Field> fieldCopy = new ThreadLocal<>();

}
