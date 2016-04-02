package cg.run;

import java.io.IOException;

import cg.render.Camera;
import cg.render.Image;
import cg.render.Ray;
import cg.render.Sphere;

public class Start {

	public static void main(String[] args) throws IOException {		
		Sphere sphere = new Sphere(1.0f);
		int width = 1920;
		int height = 1080;
		
		Image img = new Image(width, height);
		Camera cam = new Camera(90);

		System.out.println("Begin.");
		
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Ray ray = cam.rayFor(img, j, i);
				int c = 255 << 24;
				
				if (sphere.collideWith(ray).isPresent()) {
					c += (255 << 16) + (255 << 8) + 255;					
				}
				
				img.setPixel(j, i, c);
			}
		}
		
		img.writeFile("img/test.png");
		System.out.println("Done.");
	}
}
