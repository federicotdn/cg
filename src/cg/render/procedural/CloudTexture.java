package cg.render.procedural;

import cg.rand.Noise;
import cg.render.Color;
import cg.render.assets.Texture;

/**
 * Created by fede on 7/9/16.
 */
public class CloudTexture extends Texture {

    private static final Color SKY_COLOR = new Color(0 / 255, 192.0 / 255, 1);
    private final int density;

    public CloudTexture(int width, int height, int density) {
        correctGamma = false;
        this.width = width;
        this.height = height;
        this.density = density;
        pixels = new double[width * height * 4];
        fillPixels();
    }

    private void fillPixels() {
        Noise n = new Noise(width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int idx = (i + j * width) * 4;

                double t = n.turbulence(i, j, density);

                double redDiff = (1 - SKY_COLOR.getRed()) * t;
                double greenDiff = (1 - SKY_COLOR.getGreen()) * t;

                pixels[idx] = SKY_COLOR.getAlpha();
                pixels[idx + 1] = SKY_COLOR.getRed() + redDiff;
                pixels[idx + 2] = SKY_COLOR.getGreen() + greenDiff;
                pixels[idx + 3] = SKY_COLOR.getBlue();
            }
        }
    }
}
