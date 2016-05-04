package cg.rand;

public class MultiJitteredSampler {
	
	/* Correlated Multi-Jittered Sampler */
	
	public float[] xCoords;
	public float[] yCoords;
	private int size;
	
	public MultiJitteredSampler(int samples) {
		int sqrt = (int)Math.sqrt(samples);
		if (sqrt * sqrt != samples || samples < 4) {
			throw new RuntimeException("Samples must be a perfect square larger than 4.");
		}
		
		size = sqrt;
		xCoords = new float[size * size];
		yCoords = new float[size * size];
	}
	
	public int getSize() {
		return size;
	}
	
	public void generateSamples() {
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				xCoords[j * size + i] = (float)((i + (j + Math.random()) / size) / size);
				yCoords[j * size + i] = (float)((j + (i + Math.random()) / size) / size);
			}
		}
		
		for (int j = 0; j < size; j++) {
			int k = (int)(j + Math.random() * (size - j));
			for (int i = 0; i < size; i++) {
				float tmp = xCoords[j * size + i];
				xCoords[j * size + i] = xCoords[k * size + i];
				xCoords[k * size + i] = tmp;
			}
		}
		
		for (int i = 0; i < size; i++) {
			int k = (int)(i + Math.random() * (size - i));
			for (int j = 0; j < size; j++) {
				float tmp = yCoords[j * size + i];
				yCoords[j * size + i] = yCoords[j * size + k];
				yCoords[j * size + k] = tmp;
			}
		}
	}
}
