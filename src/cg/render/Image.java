package cg.render;

public class Image {
	//TODO: Add BufferedImage, File, format, etc.
	private int width;
	private int height;
	
	public Image(int width, int height) {
		this.width = width;
		this.height = height;
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
