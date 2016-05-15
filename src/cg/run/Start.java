package cg.run;

import cg.parser.SceneParser;
import cg.render.Image;
import cg.render.Scene;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Start {

	public static void main(String[] args) throws IOException {
		boolean showLegend = true;
		System.out.println("Loading scene...");
		SceneParser parser = new SceneParser("/Users/Hobbit/git/edutracing/Assets/reflection.json");
		System.out.println("Scene loaded.");
		Scene scene = parser.parseScene();

		if (scene == null) {
			return;
		}

		printSettings(scene);

		System.out.println("Begin.");
		long initialTime = System.currentTimeMillis();
		Image img = scene.render();
		long totalTime = System.currentTimeMillis() - initialTime;

		String imageInfo = "Render time: " + TimeUnit.MILLISECONDS.toMinutes(totalTime) + "m " +
				TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60 + "s " + totalTime % 1000 + "ms";
		System.out.println(imageInfo);

		if (showLegend) {
			imageInfo += " (" + img.getWidth() + "x" + img.getHeight() + "). Samples: " + scene.getSamples();
			Graphics graphics = img.getBufferedImage().getGraphics();
			graphics.setColor(new Color(0,0,0, 0.4f));
			graphics.fillRect(0, img.getHeight() - 40, img.getWidth(), 40);
			graphics.setColor(Color.WHITE);
			graphics.setFont(graphics.getFont().deriveFont(24));
			graphics.drawString(imageInfo, 15, img.getHeight() - 15);
		}

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("MM.dd_HH-mm-ss");

		img.writeFile("img/test.png");
		img.writeFile("img/" + df.format(date) + ".png");
	}

	private static void printSettings(Scene scene) {
		System.out.println("Scene loaded.");
		System.out.println("Image size: " + scene.getWidth() + "x" + scene.getHeight() + ".");
		if (scene.getSamples() == 1) {
			System.out.println("Antialiasing is disabled.");
		} else {
			System.out.println("Antialiasing set to " + scene.getSamples() + " samples.");
		}
		System.out.println("Using " + scene.getThreads() + " threads.");
		System.out.println("Bucket size: " + scene.getBucketSize() + ".");
		System.out.println("Refraction trace depth: " + scene.getRefractionTraceDepth() + ".");
		System.out.println("Reflection trace depth: " + scene.getReflectionTraceDepth() + ".");
	}
}
