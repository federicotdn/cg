package cg.render;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	List<Primitive> primitives;
	List<Light> lights; //camera, action
	
	public Scene() {
		primitives = new ArrayList<Primitive>();
	}
	
	public void addPrimitive(Primitive p) {
		primitives.add(p);
	}
	
	public void addLight(Light l) {
		
		lights.add(l);
	}
	
	public void render(Camera cam, Image img) {
		for (int p = 0; p < img.getHeight() * img.getWidth(); p++) {
				int x = p % img.getWidth();
				int y = p / img.getWidth();
				Ray ray = cam.rayFor(img, x, y);
				Color c = Color.BLACK; // black
				
				Collision col = collideRay(ray);
				if (col != null) {
					c = new Color(col.getT() / 10.0f);
				}

				img.setPixel(x, y, c);
		}
	}
	
	public Collision collideRay(Ray ray) {
		Collision last = null;
		
		for (int k = 0; k < primitives.size(); k++) {
			Collision col = primitives.get(k).collideWith(ray);
			if (col != null && (last == null || col.getT() < last.getT())) {
				last = col;
			}
		}
		
		return last;
	}
}
