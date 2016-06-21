package cg.rand;

import cg.math.Vec2;

public class MultiJitteredSampler {
	
	/* Correlated Multi-Jittered Sampler */
	
	public double[] xCoords;
	public double[] yCoords;
	private int size;
	
	public static class SubSampler {
		public double[] xCoords;
		public double[] yCoords;
		private int samples;
		private final MultiJitteredSampler base;
		
		public SubSampler(MultiJitteredSampler base, int samples) {
			if (samples > base.size * base.size) {
				throw new RuntimeException("Sample count must be equal or less than parent Sampler.");
			}
			
			this.base = base;
			this.samples = samples;
			xCoords = new double[samples];
			yCoords = new double[samples];
		}
		
		public void generateSamples() {
			for (int i = 0; i < samples; i++) {
				int index = (int)(Math.random() * (base.size * base.size));
				this.xCoords[i] = base.xCoords[index];
				this.yCoords[i] = base.yCoords[index];
			}
		}

		public Vec2 getRandomSample() {
			int index = (int)(Math.random() * samples);
			double x = xCoords[index];
			double y = yCoords[index];
			return new Vec2(x, y);
		}
		
		public int sampleCount() {
			return samples;
		}
	}
	
	public MultiJitteredSampler(int samples) {
		if (samples == 1) {
			size = 1;
			xCoords = new double[size * size];
			yCoords = new double[size * size];
			xCoords[0] = 0.5;
			yCoords[0] = 0.5;
			return;
		}
		int sqrt = (int)Math.sqrt(samples);
		if (sqrt * sqrt != samples || samples < 4) {
			throw new RuntimeException("Samples must be a perfect square larger than 4.");
		}
		
		size = sqrt;
		xCoords = new double[size * size];
		yCoords = new double[size * size];
	}
	
	public SubSampler getSubSampler(int samples) {
		return new SubSampler(this, samples);
	}
	
	public int getSize() {
		return size;
	}
	
	public Vec2 getRandomSample() {
		int index = (int)(Math.random() * (size * size));
		double x = xCoords[index];
		double y = yCoords[index];
		return new Vec2(x, y);
	}
	
	public void generateSamples() {
		if (size == 1) {
			return;
		}

		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				xCoords[j * size + i] = ((i + (j + Math.random()) / size) / size);
				yCoords[j * size + i] = ((j + (i + Math.random()) / size) / size);
			}
		}
		
		for (int j = 0; j < size; j++) {
			int k = (int)(j + Math.random() * (size - j));
			for (int i = 0; i < size; i++) {
				double tmp = xCoords[j * size + i];
				xCoords[j * size + i] = xCoords[k * size + i];
				xCoords[k * size + i] = tmp;
			}
		}
		
		for (int i = 0; i < size; i++) {
			int k = (int)(i + Math.random() * (size - i));
			for (int j = 0; j < size; j++) {
				double tmp = yCoords[j * size + i];
				yCoords[j * size + i] = yCoords[j * size + k];
				yCoords[j * size + k] = tmp;
			}
		}
	}
}
