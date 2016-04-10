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
		for (int p = 0; p < img.getHeight() * img.getWidth(); p++) {
				int x = p % img.getWidth();
				int y = p / img.getWidth();
				Ray ray = cam.rayFor(img, x, y);
				Collision last = null;
				Color c = Color.BLACK; // black
				
				for (int k = 0; k < primitives.size(); k++) {
					Collision col = primitives.get(k).collideWith(ray);
					if (col != null && (last == null || col.getT() < last.getT())) {
						last = col;
					}
				}
				
				if (last != null) {
					c = Color.WHITE; // white
				}

				img.setPixel(x, y, c);
		}
	}
}
