package cg.render;

import cg.accelerator.KDTree;
import cg.rand.MultiJitteredSampler;
import cg.rand.MultiJitteredSampler.SubSampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scene {
	private List<Primitive> primitives;
	private List<Primitive> unboundedPrimitives;

	private List<Light> lights; //camera, action
	private Camera cam;
	private KDTree kdTree;

	private Image img;
	private int samples;

	private int reflectionTraceDepth;
	private int refractionTraceDepth;

	private int threads;
	private int bucketSize;
	
	public static final Color BACKGROUND_COLOR = Color.BLACK;

	// Path Tracing Variables
	private boolean pathTracingEnabled = false;
	private int pathTracingSamples;
	private MultiJitteredSampler sampleCache = null;
	private static int SAMPLE_CACHE_SIZE = 25000000;
	
	public Scene() {
		primitives = new ArrayList<Primitive>();
		unboundedPrimitives = new ArrayList<Primitive>();
		lights = new ArrayList<Light>();
	}

	public void enablePathTracing(int samples) {
		sampleCache = new MultiJitteredSampler(SAMPLE_CACHE_SIZE);
		sampleCache.generateSamples();
		pathTracingEnabled = true;
		pathTracingSamples = samples;
	}
	
	public boolean isPathTracingEnabled() {
		return pathTracingEnabled;
	}
	
	public MultiJitteredSampler getSampleCache() {
		return sampleCache;
	}
	
	public void addPrimitive(Primitive p) {
		if (p.getBBox() != null) {
			primitives.add(p);
		} else if (p.isRenderable()) {
			unboundedPrimitives.add(p);
		}
	}

	public void setSize(int width, int height) {
		img = new Image(width, height);
	}
	
	public void setSamples(int samples) {
		this.samples = samples;
	}

	public int getSamples() {
		return this.samples;
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public void addLight(Light l) {
		lights.add(l);
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	public Image render() {
		if (cam == null || img == null) {
			throw new RuntimeException("Missing camera or height and width");
		}
		
		kdTree = new KDTree(primitives, 3 /* magic :-) */);

		Queue<Bucket> buckets = generateBuckets();
		Queue<Ray[]> rayQ = new ConcurrentLinkedQueue<>();
		Queue<MultiJitteredSampler> samplerQ = new ConcurrentLinkedQueue<>();
		Queue<MultiJitteredSampler.SubSampler> subSamplerQ = new ConcurrentLinkedQueue<>();
		
		if (pathTracingEnabled) {
			for (int i = 0; i < threads; i++) {
				rayQ.offer(new Ray[pathTracingSamples]);
			}
			
			for (int i = 0; i < threads; i++) {
				subSamplerQ.offer(new SubSampler(sampleCache, pathTracingSamples));
			}
		} else {
			for (int i = 0; i < threads; i++) {
				rayQ.offer(new Ray[samples]);
			}
			
			for (int i = 0; i < threads; i++) {
				samplerQ.offer(new MultiJitteredSampler(samples));
			}
		}

		ExecutorService pool = Executors.newFixedThreadPool(threads);

		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.poll();
			pool.execute(new Runnable() {
				@Override
				public void run() {
					Ray rays[] = rayQ.poll();

					if (pathTracingEnabled) {
						//TODO: Regenerate samples cache every now and then
						
						MultiJitteredSampler.SubSampler subSampler = subSamplerQ.poll();
						renderBucketPath(bucket, rays, subSampler);
						subSamplerQ.offer(subSampler);
					} else {						
						MultiJitteredSampler sampler = samplerQ.poll();
						renderBucket(bucket, rays, sampler);
						samplerQ.offer(sampler);
					}
					
					rayQ.offer(rays);
				}
			});
		}

		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			return img;
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	private Color colorForRays(Ray[] rays, int samples) {
		double r = 0, g = 0, b = 0;

		for (int i = 0; i < samples; i++) {
			Color c = BACKGROUND_COLOR;

			QuickCollision qc = collideRay(rays[i]);
			if (qc != null) {
				Collision col = qc.completeCollision();
				c = col.getPrimitive().getMaterial().getSurfaceColor(col, this);
			}

			r += c.getRed();
			g += c.getGreen();
			b += c.getBlue();
		}

		r /= samples;
		g /= samples;
		b /= samples;
		
		return new Color(r, g, b);
	}

	private void renderBucketPath(Bucket bucket, Ray[] rays, MultiJitteredSampler.SubSampler subSampler) {
		for (int p = 0; p < bucket.getHeight() * bucket.getWidth(); p++) {
			int x = (p % bucket.getWidth()) + bucket.getX();
			int y = (p / bucket.getWidth()) + bucket.getY();

			subSampler.generateSamples();

			cam.raysFor(rays, subSampler, img, x, y);
			img.setPixel(x, y, colorForRays(rays, pathTracingSamples));
		}		
	}
	
	private void renderBucket(Bucket bucket, Ray[] rays, MultiJitteredSampler sampler) {
		for (int p = 0; p < bucket.getHeight() * bucket.getWidth(); p++) {
			int x = (p % bucket.getWidth()) + bucket.getX();
			int y = (p / bucket.getWidth()) + bucket.getY();

			sampler.generateSamples();

			cam.raysFor(rays, sampler, img, x, y);
			img.setPixel(x, y, colorForRays(rays, samples));
		}
	}

	private Queue<Bucket> generateBuckets() {
		Queue<Bucket> bucketQueue = new ConcurrentLinkedQueue<>();

		if (threads == 1) {
			Bucket bucket = new Bucket(img.getWidth(), img.getHeight(), 0, 0);
			bucketQueue.offer(bucket);
		} else {
			for (int i = 0; i < img.getHeight(); i+= bucketSize) {
				for (int j = 0; j < img.getWidth(); j+=bucketSize) {
					Bucket bucket = new Bucket(Math.min(bucketSize, img.getWidth() - j),
							Math.min(bucketSize, img.getHeight() - i), j, i);
					bucketQueue.offer(bucket);
				}
			}
		}

		return bucketQueue;
	}
	
	public QuickCollision collideRay(Ray ray) {
		QuickCollision closestCol = kdTree.hit(ray);
		for (Primitive primitive : unboundedPrimitives) {
			QuickCollision col = primitive.quickCollideWith(ray);
			if (col == null || col.getWorldT() > ray.getMaxT()) {
				continue;
			}

			if (closestCol == null || col.getWorldT() < closestCol.getWorldT()) {
				closestCol = col;
			}
		}

		return closestCol;
	}

	public void setBucketSize(int bucketSize) {
		this.bucketSize = Math.min(bucketSize, Math.min(img.getHeight(), img.getWidth()));
	}

	public void setThreads(int threads) {
		if (threads == 0) {
			threads = Runtime.getRuntime().availableProcessors();
		}

		this.threads = threads;
	}

	public int getThreads() {
		return this.threads;
	}

	public int getBucketSize() {
		return this.bucketSize;
	}

	public int getHeight() {
		return img.getHeight();
	}

	public int getWidth() {
		return img.getWidth();
	}

	public int getReflectionTraceDepth() {
		return reflectionTraceDepth;
	}

	public void setReflectionTraceDepth(int reflectionTraceDepth) {
		this.reflectionTraceDepth = reflectionTraceDepth;
	}

	public int getRefractionTraceDepth() {
		return refractionTraceDepth;
	}

	public void setRefractionTraceDepth(int refractionTraceDepth) {
		this.refractionTraceDepth = refractionTraceDepth;
	}
}
