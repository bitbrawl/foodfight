package org.bitbrawl.foodfight.random;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import net.jcip.annotations.Immutable;

@Immutable
public final class RandomNormal implements DoubleSupplier {

	private final double mu;
	private final double sigma;

	public RandomNormal(double mu, double sigma) {
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
		return mu + ThreadLocalRandom.current().nextDouble();
	}

}
