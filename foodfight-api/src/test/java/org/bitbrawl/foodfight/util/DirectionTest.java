package org.bitbrawl.foodfight.util;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectionTest {

	@Test
	void testConstructor() {

		for (int i = 0; i < 100; i++) {
			double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2.0);
			Direction dir = new Direction(angle);
			Assertions.assertEquals(angle, dir.get(), 1e-5);
		}

		Assertions.assertEquals(0, new Direction(0).get(), 1e-5);
		Assertions.assertEquals(0, new Direction(2 * Math.PI).get(), 1e-5);
		Assertions.assertNotEquals(-Math.PI, new Direction(-Math.PI).get());

		double twoPi = 2.0 * Math.PI;
		double ulp = Double.MIN_VALUE;
		Assertions.assertEquals(0, new Direction(0).get(), ulp);
		Assertions.assertEquals(Double.MIN_VALUE, new Direction(Double.MIN_VALUE).get(), ulp);
		Assertions.assertEquals(twoPi - Double.MIN_VALUE, new Direction(-Double.MIN_VALUE).get(), ulp);

		ulp = Math.ulp(twoPi);
		Assertions.assertEquals(0, new Direction(twoPi).get(), ulp);
		Assertions.assertEquals(ulp, new Direction(twoPi + ulp).get(), ulp);
		Assertions.assertEquals(twoPi - ulp, new Direction(twoPi - ulp).get(), ulp);

		Assertions.assertThrows(IllegalArgumentException.class, () -> new Direction(Double.NaN));

	}

	@Test
	void testDifference() {

		Direction zero = Direction.EAST;
		for (int i = 0; i < 100; i++) {
			double difference = Direction.difference(zero, Direction.random());
			Assertions.assertTrue(-Math.PI <= difference && difference < Math.PI);
		}

		Assertions.assertEquals(Math.PI / 3.0,
				Direction.difference(new Direction(11.0 * Math.PI / 6.0), new Direction(Math.PI / 6.0)), 1e-5);

		Assertions.assertEquals(-Math.PI / 3.0,
				Direction.difference(new Direction(Math.PI / 6.0), new Direction(11.0 * Math.PI / 6.0)), 1e-5);

		for (int i = 0; i < 10; i++) {
			Direction alpha = Direction.random();
			Direction beta = Direction.random();
			double difference = Direction.difference(alpha, beta);
			Assertions.assertTrue(-Math.PI <= difference && difference < Math.PI);
			System.out.format("alpha: %s, beta: %s, diff: %f%n", alpha, beta, difference);
		}

	}

}
