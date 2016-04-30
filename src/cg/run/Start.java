package cg.run;

import cg.math.Vec3;
import cg.parser.SceneParser;
import cg.render.Color;
import cg.render.Image;
import cg.render.Scene;
import cg.render.lights.SpotLight;
import cg.render.materials.Lambert;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.Sphere;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Start {

	public static void main(String[] args) throws IOException {
		SceneParser parser = new SceneParser("scenes/scene1.json");
		Scene scene =  parser.parseScene();

		System.out.println("Begin.");
		long initialTime = System.currentTimeMillis();
		Image img = scene.render();
		long totalTime = System.currentTimeMillis() - initialTime;
		System.out.println("Done. Duration: " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + "m " +
				TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60 + "s");

		img.writeFile("img/test.png");
	}
	
	private static void testFillScene(Scene s) {
		int depth = 4;
		float distance = 2f;
		float colorMult = 0.1f;
		float radius = 0.8f;
		
		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < depth; j++) {
				for (int k = 0; k < depth; k++) {
					Sphere p = new Sphere(new Vec3(i * distance, j * distance, k * distance), null, null, radius);
					p.setMaterial(new Lambert(new Color(i * colorMult, j * colorMult, k * colorMult)));
					s.addPrimitive(p);
				}
			}
		}


		InfinitePlane plane = new InfinitePlane();
//		plane.setTransform(new Vec3(0, -3, 0), null, null);
		s.addPrimitive(plane);
		
		//s.addLight(new PointLight(s, Color.WHITE, 0.4f, new Vec3(-2, 10, 9)));
		
		//Searle: Icarus, how close is this to full brightness?
		//Icarus: At this distance of 36 million miles, you are observing the sun at two percent of full brightness.
		//Searle: Two percent? Can you show me four percent?
		//Icarus: Four percent would result in irreversible damage to your retinas.
		//s.addLight(new DirectionalLight(s, new Vec3(-1, -1, -1)));
		
		SpotLight l = new SpotLight(s, Color.WHITE, 0.4f, new Vec3(0,2,-10), new Vec3(0,20,0), 20);
		s.addLight(l);
	}
}
