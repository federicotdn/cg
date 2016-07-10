package cg.render.procedural;

import cg.rand.MultiJitteredSampler;
import cg.rand.Noise;
import cg.render.Color;
import cg.render.Scene;
import cg.render.assets.Texture;

/**
 * Created by fede on 7/9/16.
 */
public class VoronoiTexture extends Texture {

    private static class Cell {

        public Cell(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public int x;
        public int y;
        public Color color;
    }

    private final int samples;
    private Cell[] cells;

    public VoronoiTexture(Scene s, int width, int height, int samples, boolean bw) {
        this.width = width;
        this.height = height;
        this.samples = samples;

        MultiJitteredSampler sampler = s.getSamplerCaches().poll();
        MultiJitteredSampler.SubSampler subSampler = sampler.getSubSampler(samples);
        subSampler.generateSamples();
        s.getSamplerCaches().offer(sampler);

        cells = new Cell[samples];

        for (int i = 0; i < samples; i++) {
            int x = (int) (subSampler.xCoords[i] * width);
            int y = (int) (subSampler.yCoords[i] * height);

            Color c;
            if (bw) {
                c = new Color(Math.random());
            } else {
                double r = Math.random();
                double g = Math.random();
                double b = Math.random();
                c = new Color(r, g, b);
            }

            cells[i] = new Cell(x, y, c);
        }

        pixels = new double[width * height * 4];
        fillPixels();
    }

    private void fillPixels() {
        Noise n = new Noise(width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int idx = (i + j * width) * 4;

                Cell nearest = null;
                double minDist = Double.MAX_VALUE;

                for (int k = 0; k < samples; k++) {
                    Cell cell = cells[k];

                    double xDist = Math.abs(cell.x - i);
                    double yDist = Math.abs(cell.y - j);
                    double dist = Math.sqrt(xDist * xDist + yDist * yDist);

                    if (dist < minDist) {
                        nearest = cell;
                        minDist = dist;
                    }
                }

                Color c = nearest.color;

                pixels[idx] = c.getAlpha();
                pixels[idx + 1] = c.getRed();
                pixels[idx + 2] = c.getGreen();
                pixels[idx + 3] = c.getBlue();
            }
        }
    }
}
