package cg.run;

import cg.math.Vec3;
import cg.render.Camera;
import cg.render.Image;
import cg.render.Scene;
import cg.render.lights.PointLight;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.Sphere;

import java.io.IOException;

public class Start {

	public static void main(String[] args) throws IOException {		
		int width = 300;
		int height = 300;
		
		Scene scene = new Scene();
		Image img = new Image(width, height);
		Camera cam = new Camera(new Vec3(2.2f, 5.0f, 10), new Vec3(-20,0,0), 60);

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
					Sphere p = new Sphere(new Vec3(i * 1.3f, j * 1.3f, k * 1.3f), null, null, 0.6f);
					s.addPrimitive(p);
				}
			}
		}

		InfinitePlane plane = new InfinitePlane();
		plane.setTransform(new Vec3(0, -3, 0), null, null);
		s.addPrimitive(plane);
		
		s.addLight(new PointLight(new Vec3(0, 5, 7)));
	}
}
