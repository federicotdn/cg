package cg.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	private final String FORMAT = "png";
	
	private int width;
	private int height;
	private BufferedImage buffer;
	
	public Image(int width, int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.width = width;
		this.height = height;
	}
	
	public void setPixel(int x, int y, int rgb) {
		buffer.setRGB(x,  y, rgb);
	}
	
	public void writeFile(String filename) throws IOException {
		File f = new File(filename);
		ImageIO.write(buffer, FORMAT, f);
	}
	
	public float aspectRatio() {
		return ((float)width) / height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
