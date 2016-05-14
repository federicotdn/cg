package cg.render;

import cg.accelerator.KDTree;
import cg.rand.MultiJitteredSampler;

import java.util.ArrayList;
import java.util.List;

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
	
	public final Color BACKGROUND_COLOR = Color.BLACK;

	public Scene() {
		primitives = new ArrayList<Primitive>();
		unboundedPrimitives = new ArrayList<Primitive>();
		lights = new ArrayList<Light>();
	}

	public void addPrimitive(Primitive p) {
		if (p.getBBox() != null) {
			primitives.add(p);
		} else {
			unboundedPrimitives.add(p);
		}
	}

	public void setSize(int width, int height) {
		img = new Image(width, height);
	}
	
	public void setSamples(int samples) {
		this.samples = samples;
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
		
		kdTree = new KDTree(primitives, 5);
		long count = 0;
		Ray[] rays = new Ray[samples];
		
		MultiJitteredSampler sampler;
		if (samples == 1) {
			sampler = null;
			System.out.println("Antialiasing is disabled.");
		} else {
			sampler = new MultiJitteredSampler(samples);
		}
		
		for (int p = 0; p < img.getHeight() * img.getWidth(); p++) {
			int x = p % img.getWidth();
			int y = p / img.getWidth();
			
			if (sampler != null) {				
				sampler.generateSamples();
			}
			cam.raysFor(rays, sampler, img, x, y);
			
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

//				if (col != null) {
//					r += Math.abs(col.getNormal().x);
//					g += Math.abs(col.getNormal().y);
//					b += Math.abs(col.getNormal().z);
//				}
			}
			
			//TODO: Change average function for Monte Carlo estimator?
			r /= samples;
			g /= samples;
			b /= samples;

			img.setPixel(x, y, new Color(r, g, b));
			count++;

			if (count % (img.getHeight() * img.getWidth() / 100) == 0) {
				System.out.println((int)((double)count / (img.getWidth() * img.getHeight()) * 100)  + " %");
			}
		}

		return img;
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
