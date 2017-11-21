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

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class JavaPlayer extends DynamicPlayer {

	private final Logger logger;
	private final Field field;
	private final Clock clock = new Clock(Field.TIME_LIMIT, TimeUnit.MINUTES);

	private final Lock turnLock = new ReentrantLock();

	protected JavaPlayer() {
		super(playerCopy);
		logger = staticLogger;
		field = staticField;
	}

	protected abstract Action playTurn(int turnNumber);

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "-" + getTeam().getSymbol() + getSymbol();
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

	Action playTurnInternal(int turnNumber) throws InterruptedException, ExecutionException, TimeoutException {
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
					Action action = playTurn(turnNumber);
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

	static JavaPlayer newPlayer(Player playerCopy, Class<? extends JavaPlayer> clazz, Logger logger, Field field)
			throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		if (constructorLock.tryLock())
			try {
				JavaPlayer.playerCopy = playerCopy;
				JavaPlayer.staticLogger = logger;
				JavaPlayer.staticField = field;
				return clazz.getConstructor().newInstance();
			} finally {
				constructorLock.unlock();
			}
		else
			throw new IllegalStateException("Another JavaPlayer is already being created");
	}

	private static final Lock constructorLock = new ReentrantLock();
	private static Player playerCopy;
	private static Logger staticLogger;
	private static Field staticField;

}
