package cg.run;

import cg.parser.SceneParser;
import cg.render.Image;
import cg.render.Scene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Start {

	public static void main(String[] args) throws IOException {
		System.out.println("Loading scene...");
		SceneParser parser = new SceneParser("scenes/simplescene4.json");
		System.out.println("Scene loaded.");
		Scene scene = parser.parseScene();

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
}
