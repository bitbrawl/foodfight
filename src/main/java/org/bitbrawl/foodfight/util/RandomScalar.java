package org.bitbrawl.foodfight.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import net.jcip.annotations.Immutable;

@Immutable
public final class RandomScalar implements DoubleSupplier {

	private final double mu;
	private final double sigma;

	public RandomScalar(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
	}

	public double getMu() {
		return mu;
	}

	public double getSigma() {
		return sigma;
	}

	@Override
	public double getAsDouble() {
		return mu * Math.exp(ThreadLocalRandom.current().nextGaussian() * sigma / mu);
	}

	@Override
	public String toString() {
		return "RandomScalar[mu=" + mu + ",sigma=" + sigma + ']';
	}
}
