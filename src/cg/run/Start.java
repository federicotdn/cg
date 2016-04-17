package cg.run;

import cg.math.Vec3;
import cg.render.Camera;
import cg.render.Color;
import cg.render.Image;
import cg.render.Scene;
import cg.render.lights.DirectionalLight;
import cg.render.lights.PointLight;
import cg.render.materials.Lambert;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.Sphere;

import java.io.IOException;

public class Start {

	public static void main(String[] args) throws IOException {		
		int width = 600;
		int height = 600;
		
		Image img = new Image(width, height);
		Camera cam = new Camera(new Vec3(3, 5.0f, 12), new Vec3(-20,0,0), 60);
		Scene scene = new Scene(cam);

		testFillScene(scene);
		
		System.out.println("Begin.");
		scene.render(img);
		System.out.println("Done.");
		
		img.writeFile("img/test.png");
	}
	
	private static void testFillScene(Scene s) {
		int depth = 4;

		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < depth; j++) {
				for (int k = 0; k < depth; k++) {
					Sphere p = new Sphere(new Vec3(i * 1.3f, j * 1.3f, k * 1.3f), null, null, 0.6f);
					p.setMaterial(new Lambert(new Color(i * 0.2f, j * 0.2f, k * 0.2f)));
					s.addPrimitive(p);
				}
			}
		}

		InfinitePlane plane = new InfinitePlane();
		plane.setTransform(new Vec3(0, -3, 0), null, null);
		s.addPrimitive(plane);
		
		//s.addLight(new PointLight(new Vec3(-2, 6, 9)));
		
		//Searle: Icarus, how close is this to full brightness?
		//Icarus: At this distance of 36 million miles, you are observing the sun at two percent of full brightness.
		//Searle: Two percent? Can you show me four percent?
		//Icarus: Four percent would result in irreversible damage to your retinas.
		s.addLight(new DirectionalLight(s, new Vec3(-1, -1, -1)));
	}
}
