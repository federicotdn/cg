package cg.render.procedural;

import cg.rand.Noise;
import cg.render.Color;
import cg.render.assets.Texture;

/**
 * Created by fede on 7/9/16.
 */
public class MarbleTexture extends Texture {

    private static final int DEFAULT_TURBULENCE = 128;

    public MarbleTexture(int width, int height) {
        correctGamma = false;
        this.width = width;
        this.height = height;
        pixels = new double[width * height * 4];
        fillPixels();
    }

    private void fillPixels() {
        Noise n = new Noise(width, height);

        double xPeriod = 5;
        double yPeriod = 10;
        double turbPower = 4;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int idx = (i + j * width) * 4;

                double t = n.turbulence(i, j, DEFAULT_TURBULENCE);
                double xyValue = i * (xPeriod / width) + j * (yPeriod / height) + turbPower * t;
                double sinValue = Math.abs(Math.sin(xyValue * Math.PI));

                Color c = new Color(sinValue);

                pixels[idx] = c.getAlpha();
                pixels[idx + 1] = c.getRed();
                pixels[idx + 2] = c.getGreen();
                pixels[idx + 3] = c.getBlue();
            }
        }
    }


}
