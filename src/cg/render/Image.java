package cg.render;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class Image {
	private final String FORMAT = "png";
	
	private int width;
	private int height;
	private double[] pixels;
	private BufferedImage buffer;
	
	public Image(int width, int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.width = width;
		this.height = height;
		this.pixels = new double[4 * width * height];
	}
	
	public void setPixel(int x, int y, Color color) {
		int index =( (y * width) + x) * 4;
		setPixel(index, color);
	}

	private void setPixel(int p, Color color) {
		pixels[p] = color.getAlpha();
		pixels[p + 1] = color.getRed();
		pixels[p + 2] = color.getGreen();
		pixels[p + 3] = color.getBlue();
	}
	
	public void writeFile(String filename) throws IOException {
		File f = new File(filename);
		ImageIO.write(getBufferedImage(), FORMAT, f);
	}

	private BufferedImage getBufferedImage() {
		int[] data = new int[pixels.length / 4];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] = ((doubleToInt(pixels[j]) << 24) | doubleToInt(pixels[j + 1]) << 16) | (doubleToInt(pixels[j + 2]) << 8) | (doubleToInt(pixels[j + 3]));
			j += 4;
		}

		SampleModel sm = buffer.getSampleModel();
		WritableRaster raster = Raster.createWritableRaster(sm, new DataBufferInt(data, data.length), null);
		buffer.setData(raster);

		return buffer;
	}

	private int doubleToInt(double pixel) {
		byte ans = (byte) Math.round((pixel * 255));
		return byteToUnsigned(ans);
	}

	private int byteToUnsigned(byte b) {
		return b & 0xFF;
	}
	
	public double aspectRatio() {
		return ((double)width) / height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
