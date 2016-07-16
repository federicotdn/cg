package cg.render.assets;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.render.Color;
import cg.render.Image;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Texture {
	protected int width;
	protected int height;
	protected double[] pixels;
	protected boolean correctGamma;

	protected Texture() {
		/* EMPTY */
	}
	
	public Texture(byte[] data, boolean gammaEnabled) throws IOException {
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
		
		boolean isSRGB = img.getColorModel().getColorSpace().isCS_sRGB();
		correctGamma = gammaEnabled && isSRGB;
		
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
			
			double red = intToDouble(data[i + 3]);
			double green = intToDouble(data[i + 2]);
			double blue = intToDouble(data[i + 1]);
			
			if (correctGamma) {
				double invGamma = 1 / Image.GAMMA;
				red = Math.pow(red, invGamma);
				green = Math.pow(green, invGamma);
				blue = Math.pow(blue, invGamma);
			}
			
			pixels[j + 1] = red;
			pixels[j + 2] = green;
			pixels[j + 3] = blue;

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
}
