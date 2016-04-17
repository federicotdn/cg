package cg.render;

import cg.accelerator.KDTree;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	private List<Primitive> primitives;
	private List<Light> lights; //camera, action
	private Camera cam;
	private KDTree kdTree;
	
	private final Color BACKGROUND_COLOR = Color.BLACK;
	
	public Scene(Camera cam) {
		this.cam = cam;
		primitives = new ArrayList<Primitive>();
		lights = new ArrayList<Light>();
	}
	
	public void addPrimitive(Primitive p) {
		primitives.add(p);
	}
	
	public void addLight(Light l) {
		lights.add(l);
	}
	
	public Camera getCamera() {
		return cam;
	}

	public void render(Image img) {
		long count = 0;
		kdTree = new KDTree(primitives, 5, 0);
		for (int p = 0; p < img.getHeight() * img.getWidth(); p++) {
			int x = p % img.getWidth();
			int y = p / img.getWidth();
			Ray ray = cam.rayFor(img, x, y);
			Color c = BACKGROUND_COLOR;

			Collision col = collideRay(ray);
			if (col != null) {
				c = getSurfaceColor(col);
			}

			img.setPixel(x, y, c);
			count++;

			if (count % (img.getHeight() * img.getWidth() /20) == 0) {
				System.out.println((int)((float)count / (img.getWidth() * img.getHeight()) * 100)  + " %");
			}
		}
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
		return kdTree.hit(ray);
	}
}
