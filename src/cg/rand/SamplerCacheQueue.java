package cg.rand;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SamplerCacheQueue {
	private Queue<MultiJitteredSampler> samplerQueue;
	private final int samplesPerSampler;
	
	public SamplerCacheQueue(int samplerCount, int samplesPerSampler) {
		samplerQueue = new ConcurrentLinkedQueue<MultiJitteredSampler>();
		this.samplesPerSampler = samplesPerSampler;

		for (int i = 0; i < samplerCount; i++) {
			MultiJitteredSampler sampler = new MultiJitteredSampler(samplesPerSampler);
			sampler.generateSamples();
			samplerQueue.offer(sampler);
		}
	}
	
	public MultiJitteredSampler poll() {
		MultiJitteredSampler sampler = samplerQueue.poll();
		if (sampler == null) {
			sampler = new MultiJitteredSampler(samplesPerSampler);
			sampler.generateSamples();
		}
		
		return sampler;
	}
	
	public void offer(MultiJitteredSampler sampler) {
		samplerQueue.offer(sampler);
	}
}
