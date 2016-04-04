package cg.run;

import java.io.IOException;

import cg.math.Vec3;
import cg.render.Camera;
import cg.render.Image;
import cg.render.Scene;
import cg.render.Sphere;

public class Start {

	public static void main(String[] args) throws IOException {		
		int width = 1920;
		int height = 1080;
		
		Scene scene = new Scene();
		Image img = new Image(width, height);
		Camera cam = new Camera(new Vec3(3, 0, -3), new Vec3(10,0,0), 90);

		testFillScene(scene);
		
		System.out.println("Begin.");
		scene.render(cam, img);
		System.out.println("Done.");
		
		img.writeFile("img/test.png");
	}
	
	private static void testFillScene(Scene s) {
		int depth = 4;

		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < depth; j++) {
				for (int k = 0; k < depth; k++) {
					Sphere p = new Sphere(0.6f);
					p.setTransform(new Vec3(i * 1.3f, j * 1.3f, k * 1.3f), null, null);
					s.addPrimitive(p);
				}
			}
		}
	}
}
