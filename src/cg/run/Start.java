package cg.run;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Start {

	public static void main(String[] args) {
		System.out.println("test");
		BufferedImage img;
		
		try {
			String url = "https://ih0.redbubble.net/image.86009801.7137/flat,800x800,075,f.jpg";
			img = ImageIO.read(new URL(url));
			ImageIO.write(img, "png", new File("test.png"));
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
