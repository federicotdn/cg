package cg.run;

import cg.math.Vec3;
import cg.parser.SceneParser;
import cg.render.Color;
import cg.render.Image;
import cg.render.Scene;
import cg.render.lights.DirectionalLight;
import cg.render.lights.PointLight;
import cg.render.lights.SpotLight;
import cg.render.materials.Lambert;
import cg.render.shapes.Box;
import cg.render.shapes.FinitePlane;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.Sphere;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Start {

	public static void main(String[] args) throws IOException {
		System.out.println("Loading scene...");
		SceneParser parser = new SceneParser("scenes/simplescene.json");
		System.out.println("Scene loaded.");
		Scene scene =  parser.parseScene();

		testModifyScene(scene); //TODO: Remove
		
		System.out.println("Begin.");
		long initialTime = System.currentTimeMillis();
		Image img = scene.render();
		long totalTime = System.currentTimeMillis() - initialTime;
		System.out.println("Done. Duration: " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + "m " +
				TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60 + "s");
		
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("MM.dd_HH-mm-ss");

		img.writeFile("img/test.png");
		img.writeFile("img/" + df.format(date) + ".png");
	}
	
	private static void testModifyScene(Scene s) {
		
	}
	
	private static void testFillScene(Scene s) {
		int depth = 4;
		float distance = 2f;
		float colorMult = 0.1f;
		float radius = 0.8f;
		
		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < depth; j++) {
				for (int k = 0; k < depth; k++) {
					//Sphere p = new Sphere(new Vec3(i * distance, j * distance, k * distance), null, null, radius);
					Box p = new Box(1,1,1);
					//p.setTransform(new Vec3(i * distance, j * distance, k * distance), null, null);
					p.setMaterial(new Lambert(new Color(i * colorMult, j * colorMult, k * colorMult)));
					s.addPrimitive(p);
				}
			}
		}
		
		//InfinitePlane plane = new InfinitePlane();
		FinitePlane plane = new FinitePlane(70, 100);
		//plane.setTransform(new Vec3(0, -5, 0), null, null);

		s.addPrimitive(plane);
		
		//s.addLight(new PointLight(s, Color.WHITE, 0.4f, new Vec3(-2, 10, 9)));
		
		//Searle: Icarus, how close is this to full brightness?
		//Icarus: At this distance of 36 million miles, you are observing the sun at two percent of full brightness.
		//Searle: Two percent? Can you show me four percent?
		//Icarus: Four percent would result in irreversible damage to your retinas.
		// S U N S H I N E
		s.addLight(new DirectionalLight(s, new Vec3(45, 0, 45)));
		
		SpotLight l = new SpotLight(s, Color.WHITE, 0.4f, new Vec3(0,5,-10), new Vec3(30,20,0), 20);
		PointLight j = new PointLight(s, new Vec3(-4,10,-4));
		//s.addLight(l);
		s.addLight(j);
	}
}
