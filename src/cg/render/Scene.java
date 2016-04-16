package cg.render;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	List<Primitive> primitives;
	List<Light> lights; //camera, action
	
	private final Color BACKGROUND_COLOR = Color.BLACK;
	
	public Scene() {
		primitives = new ArrayList<Primitive>();
		lights = new ArrayList<Light>();
	}
	
	public void addPrimitive(Primitive p) {
		primitives.add(p);
	}
	
	public void addLight(Light l) {
		l.setScene(this);
		lights.add(l);
	}

	public void render(Camera cam, Image img) {
		long count = 0;
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
		Color c = new Color(0.05f);
		
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
		Collision last = null;
		
		for (Primitive p : primitives) {
			Collision col = p.collideWith(ray);
			if (col == null || col.getT() > ray.getMaxT()) {
				continue;
			}
			
			if (last == null || col.getT() < last.getT()) {
				last = col;
			}
		}
		
		return last;
	}
}
