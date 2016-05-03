package cg.render.assets;

import cg.render.Color;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Texture {
	private int width;
	private int height;
	private float[] pixels;

	public Texture(byte[] data) {
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
			this.width = img.getWidth();
			this.height = img.getHeight();
			pixels = new float[width * height * 4];
			if (img.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
				throw new IllegalArgumentException("Invalid image format");
			}
			transformABGR4Byte(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void transformABGR4Byte(BufferedImage bufferedImage) {
		byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

		int j = 0;
		for (int i = 0; i < data.length; i += 4) {
			pixels[j] = intToFloat(data[i]);
			pixels[j + 1] = intToFloat(data[i + 3]);
			pixels[j + 2] = intToFloat(data[i + 2]);
			pixels[j + 3] = intToFloat(data[i + 1]);

			j += 4;
		}
	}

	private float intToFloat(int i) {
		return (i & 0xff)/255.0f;
	}

	public Color getSample(float u, float v) {
		int x = (int)(width * u);
		int y = (int) (height * v);
		int index =( (y * width) + x) * 4;

		return new Color(pixels[index], pixels[index + 1], pixels[index + 2], pixels[index + 3]);
	}

	// For debugging purposes. PLEASE REMOVE LATER
	// *******************************************

	private BufferedImage getBufferedImage() {
		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] data = new int[pixels.length / 4];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			data[i] = ((floatToInt(pixels[j]) << 24) | floatToInt(pixels[j + 1]) << 16) | (floatToInt(pixels[j + 2]) << 8) | (floatToInt(pixels[j + 3]));
			j += 4;
		}

		SampleModel sm = buffer.getSampleModel();
		WritableRaster raster = Raster.createWritableRaster(sm, new DataBufferInt(data, data.length), null);
		buffer.setData(raster);

		return buffer;
	}

	private int floatToInt(float pixel) {
		byte ans = (byte) Math.round((pixel * 255));
		return byteToUnsigned(ans);
	}

	private int byteToUnsigned(byte b) {
		return b & 0xFF;
	}

	// *******************************************
}
