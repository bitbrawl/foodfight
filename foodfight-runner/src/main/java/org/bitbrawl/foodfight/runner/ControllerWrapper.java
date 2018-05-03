package org.bitbrawl.foodfight.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Clock;
import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.ControllerException;
import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.logging.ControllerLogger;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class ControllerWrapper implements Controller {

	private final JavaController wrapped;
	private volatile int turnNumber;

	public ControllerWrapper(Class<? extends JavaController> clazz) throws InterruptedException, ControllerException {

		ControllerClock clock = new ControllerClock(Clock.TIME_LIMIT_NANOS, TimeUnit.NANOSECONDS);
		Logger logger = new ControllerLogger(() -> turnNumber);

		wrapped = runTimed(() -> {
			@SuppressWarnings("deprecation")
			JavaController result = JavaController.newInstance(clazz, logger, clock);
			return result;
		}, clock);

	}

	@Override
	public Action playAction(Field field, Team team, Player player) {

		try {
			return runTimed(() -> wrapped.playAction(field, team, player), (ControllerClock) wrapped.getClock());
		} catch (InterruptedException e) {
			wrapped.getLogger().log(Level.SEVERE, "Running thread interrupted", e);
			return null;
		} catch (ControllerException e) {
			wrapped.getLogger().log(Level.SEVERE, "Unable to play turn for player", e);
			return null;
		}

	}

	private static <T> T runTimed(Callable<T> body, ControllerClock clock)
			throws InterruptedException, ControllerException {

		int parallelism = Runtime.getRuntime().availableProcessors();
		ForkJoinWorkerThreadFactory factory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
		ForkJoinPool pool = new ForkJoinPool(parallelism, factory, null, true);

		long timeLeftSeconds = clock.getTimeLeft(TimeUnit.SECONDS);

		try {

			T result = pool.submit(() -> {
				assert pool.getActiveThreadCount() == 1;
				clock.start();

				T t = body.call();

				if (pool.getActiveThreadCount() == 1)
					clock.end();
				return t;

			}).get(timeLeftSeconds + 2L, TimeUnit.SECONDS);

			timeLeftSeconds = clock.getTimeLeft(TimeUnit.SECONDS);
			pool.shutdownNow();
			if (!pool.awaitTermination(timeLeftSeconds + 2L, TimeUnit.SECONDS))
				throw new TimeoutException("Took too long for the pool to shut down");
			clock.end();
			if (clock.getTimeLeft(TimeUnit.NANOSECONDS) < 0)
				throw new TimeoutException("Ran out of time");

			return result;

		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ControllerException)
				throw (ControllerException) cause;
			throw new ControllerException("Controller threw an exception", e);
		} catch (TimeoutException e) {
			throw new ControllerException("Ran out of time", e);
		} finally {
			clock.end();
		}

	}

}
