package org.bitbrawl.foodfight.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import net.jcip.annotations.ThreadSafe;

/**
 * A random variable used to add a bit of randomness to various parts of the
 * game. This continuous random variable has two positive parameters &mu; and
 * &sigma;, returned by {@link #getMu()} and {@link #getSigma()}. The
 * distribution of these random variables is close to that of a normal
 * distribution, with a mean of &mu; and a standard deviation of &sigma;, but
 * modified a bit to ensure that values will never be negative. The true value,
 * if Z is a standard normal random variable, is &mu; &times; e<sup>Z &times;
 * &sigma; / &mu;</sup>. Note that the median value of a random scalar is
 * exactly &mu;, while the mean or expected value is a bit higher.
 * 
 * @author Finn
 */
@ThreadSafe
public final class RandomScalar implements DoubleSupplier {

	private final double mu;
	private final double sigma;

	/**
	 * Creates a random scalar with the given &mu; and &sigma;.
	 * 
	 * @param mu
	 *            the &mu; value for the random scalar
	 * @param sigma
	 *            the &sigma; value for the random scalar
	 * @throws IllegalArgumentException
	 *             if mu or sigma is not finite and positive
	 */
	public RandomScalar(double mu, double sigma) {
		if (!Double.isFinite(mu))
			throw new IllegalArgumentException("mu must be finite");
		if (!Double.isFinite(sigma))
			throw new IllegalArgumentException("sigma must be finite");
		if (mu <= 0)
			throw new IllegalArgumentException("mu must be positive");
		if (sigma <= 0)
			throw new IllegalArgumentException("sigma must be positive");

		this.mu = mu;
		this.sigma = sigma;

	}

	/**
	 * Returns the &mu; value for this random scalar.
	 * 
	 * @return this random scalar's &mu; value
	 */
	public double getMu() {
		return mu;
	}

	/**
	 * Returns the &sigma; value for this random scalar.
	 * 
	 * @return this random scalar's &sigma; value
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * Generates a value from the continuous distribution represented by this
	 * random scalar. Note that this method will return different values when
	 * called multiple times.
	 * 
	 * @return a value from this distribution
	 */
	@Override
	public double getAsDouble() {
		return mu * Math.exp(ThreadLocalRandom.current().nextGaussian() * sigma / mu);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RandomScalar))
			return false;
		RandomScalar other = (RandomScalar) obj;
		return mu == other.mu && sigma == other.sigma;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(mu) * 31 + Double.hashCode(sigma);
	}

	@Override
	public String toString() {
		return "RandomScalar[mu=" + mu + ",sigma=" + sigma + ']';
	}
}
