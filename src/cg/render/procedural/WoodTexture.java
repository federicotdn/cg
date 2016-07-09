package cg.render.procedural;

import cg.rand.Noise;
import cg.render.Color;
import cg.render.assets.Texture;

/**
 * Created by fede on 7/9/16.
 */
public class WoodTexture extends Texture {

    private static final Color WOOD_BASE = new Color(80.0 / 255, 30.0 / 255, 30.0 / 255);
    private static final int DEFAULT_TURBULENCE = 32;
    private static final double DEFAULT_POWER = 0.035;
    private final double rings;

    public WoodTexture(int width, int height, double rings) {
        correctGamma = false;
        this.width = width;
        this.height = height;
        this.rings = rings;
        pixels = new double[width * height * 4];
        fillPixels();
    }

    private void fillPixels() {
        Noise n = new Noise(width, height);

        double xyPeriod = rings;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int idx = (i + j * width) * 4;

                double t = n.turbulence(i, j, DEFAULT_TURBULENCE);
                double xValue = (i - width / 2) / (double)width;
                double yValue = (j - height / 2) / (double)height;
                double distValue = Math.sqrt(xValue * xValue + yValue * yValue) + DEFAULT_POWER * t;
                double sinValue = (Math.abs(Math.sin(2 * xyPeriod * distValue * Math.PI))) / 2;

                pixels[idx] = WOOD_BASE.getAlpha();
                pixels[idx + 1] = WOOD_BASE.getRed() + sinValue;
                pixels[idx + 2] = WOOD_BASE.getGreen() + sinValue;
                pixels[idx + 3] = WOOD_BASE.getBlue();
            }
        }
    }
}
