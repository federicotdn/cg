package cg.render;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	List<Primitive> primitives;
	
	public Scene() {
		primitives = new ArrayList<Primitive>();
	}
	
	public void addPrimitive(Primitive p) {
		primitives.add(p);
	}
	
	public void render(Camera cam, Image img) {
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Ray ray = cam.rayFor(img, j, i);
				Collision last = null;
				int c = argbGray(0); // black
				
				for (int k = 0; k < primitives.size(); k++) {
					Collision col = primitives.get(k).collideWith(ray);
					if (col != null && (last == null || col.getT() < last.getT())) {
						last = col;
					}
				}
				
				if (last != null) {
					c = argbGray(1.0f); // white
				}
				
				img.setPixel(j, i, c);
			}
		}
	}
	
	private int argbGray(float v) {
		int c = 255 << 24;
		c += ((int)(255 * v)) << 16;
		c += ((int)(255 * v)) << 8;
		c += (int)(255 * v);
		return c;
	}
}
