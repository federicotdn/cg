package cg.render;

import cg.accelerator.KDTree;

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
		
		for (int p = 0; p < img.getHeight() * img.getWidth(); p++) {
			int x = p % img.getWidth();
			int y = p / img.getWidth();
			Ray ray = cam.rayFor(img, x, y);
			Color c = BACKGROUND_COLOR;

			Collision col = collideRay(ray);
			if (col != null) {
				// Debug: normals as colors
				//Vec3 normal = col.getNormal();
				//c = new Color(Math.abs(normal.x), Math.abs(normal.y), Math.abs(normal.z));	
				
				c = getSurfaceColor(col);
			}

			img.setPixel(x, y, c);
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
