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
	
	private final Color BACKGROUND_COLOR = Color.BLACK;

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
			
			float r = 0, g = 0, b = 0;
			
			for (int i = 0; i < samples; i++) {
				Color c = BACKGROUND_COLOR;
				
				Collision col = collideRay(rays[i]);
				if (col != null) {
					c = getSurfaceColor(col);
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
			
			r /= samples;
			g /= samples;
			b /= samples;

			img.setPixel(x, y, new Color(r, g, b));
			count++;

			if (count % (img.getHeight() * img.getWidth() /20) == 0) {
				System.out.println((int)((float)count / (img.getWidth() * img.getHeight()) * 100)  + " %");
			}
		}

		return img;
	}
	
	public Color getSurfaceColor(Collision col) {
		Color c = Color.BLACK;
		
		for (Light light : lights) {
			Color i = light.illuminateSurface(col);
			if (i == null) {
				continue;
			}
			
			c = c.sum(i);
		}

		return c;
	}
	
	public Collision collideRay(Ray ray) {
		Collision closestCol = kdTree.hit(ray);
		for (Primitive primitive : unboundedPrimitives) {
			Collision col = primitive.collideWith(ray);
			if (col == null || col.getT() > ray.getMaxT()) {
				continue;
			}

			if (closestCol == null || col.getT() < closestCol.getT()) {
				closestCol = col;
			}
		}
		return closestCol;
	}
}
