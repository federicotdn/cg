package cg.render.assets;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.render.Color;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Texture {
	private int width;
	private int height;
	private double[] pixels;

	protected Texture() {
		/* EMPTY */
	}
	
	public Texture(byte[] data) throws IOException {
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
		this.width = img.getWidth();
		this.height = img.getHeight();
		pixels = new double[width * height * 4];
		if (img.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
			throw new IllegalArgumentException("Invalid image format");
		}
		transformABGR4Byte(img);
	}

	private void transformABGR4Byte(BufferedImage bufferedImage) {
		byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

		int j = 0;
		for (int i = 0; i < data.length; i += 4) {
			pixels[j] = intToDouble(data[i]);
			pixels[j + 1] = intToDouble(data[i + 3]);
			pixels[j + 2] = intToDouble(data[i + 2]);
			pixels[j + 3] = intToDouble(data[i + 1]);

			j += 4;
		}
	}

	private double intToDouble(int i) {
		return (i & 0xff)/255.0f;
	}

	public Color getSample(double u, double v) {
		u = MathUtils.clamp(u);
		v = 1 - MathUtils.clamp(v);
		int x = (int)(width * u);
		int y = (int) (height * v);
		int index =( (y * width) + x) * 4;
		index = (int) MathUtils.clamp(index, 0, pixels.length - 4);

		return new Color(pixels[index], pixels[index + 1], pixels[index + 2], pixels[index + 3]);
	}
	
	public Color getOffsetScaledSample(Vec2 offset, Vec2 scale, double u, double v) {
		double newU = (u * scale.x) + offset.x;
		double newV = (v * scale.y) + offset.y;
		newU = repeatUV(newU);
		newV = repeatUV(newV);
		return getSample(newU, newV);
	}
	
	private double repeatUV(double coord) {
		if (coord >= 0) {
			return coord % 1;
		}
		return 1 - (Math.abs(coord) % 1);
	}

	// For debugging purposes. PLEASE REMOVE LATER
	// *******************************************

//	private BufferedImage getBufferedImage() {
//		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		int[] data = new int[pixels.length / 4];
//		int j = 0;
//		for (int i = 0; i < data.length; i++) {
//			data[i] = ((doubleToInt(pixels[j]) << 24) | doubleToInt(pixels[j + 1]) << 16) | (doubleToInt(pixels[j + 2]) << 8) | (doubleToInt(pixels[j + 3]));
//			j += 4;
//		}
//
//		SampleModel sm = buffer.getSampleModel();
//		WritableRaster raster = Raster.createWritableRaster(sm, new DataBufferInt(data, data.length), null);
//		buffer.setData(raster);
//
//		return buffer;
//	}

//	private int doubleToInt(double pixel) {
//		byte ans = (byte) Math.round((pixel * 255));
//		return byteToUnsigned(ans);
//	}
//
//	private int byteToUnsigned(byte b) {
//		return b & 0xFF;
//	}

	// *******************************************
}
