package cg.render;

import cg.math.MathUtils;

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
	private boolean gammaCorrect = false;
	public static final double GAMMA = 1/2.2;
	
	public Image(int width, int height) {
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
		
		double red = MathUtils.clamp(color.getRed());
		double green = MathUtils.clamp(color.getGreen());
		double blue = MathUtils.clamp(color.getBlue());
		
		pixels[p + 1] = red;
		pixels[p + 2] = green;
		pixels[p + 3] = blue;
	}
	
	public void enableGammaCorrection() {
		gammaCorrect = true;
	}
	
	public void writeFile(String filename) throws IOException {
		File f = new File(filename);
		ImageIO.write(getBufferedImage(), FORMAT, f);
	}

	public BufferedImage getBufferedImage() {
		if (buffer == null) {
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int[] data = new int[pixels.length / 4];
			int j = 0;
			for (int i = 0; i < data.length; i++) {
				double alpha = pixels[j];
				double red = pixels[j + 1];
				double green = pixels[j + 2];
				double blue = pixels[j + 3];
				
				if (gammaCorrect) {
					red = MathUtils.clamp(Math.pow(red, GAMMA));
					green = MathUtils.clamp(Math.pow(green, GAMMA));
					blue = MathUtils.clamp(Math.pow(blue, GAMMA));
				}
				
				data[i] = ((doubleToInt(alpha) << 24) | doubleToInt(red) << 16) | (doubleToInt(green) << 8) | (doubleToInt(blue));
				j += 4;
			}

			SampleModel sm = buffer.getSampleModel();
			WritableRaster raster = Raster.createWritableRaster(sm, new DataBufferInt(data, data.length), null);
			buffer.setData(raster);
		}

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

	public boolean isGammaCorrect() {
		return gammaCorrect;
	}
}
