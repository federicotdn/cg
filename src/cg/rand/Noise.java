package cg.rand;

/**
 * Created by fede on 7/9/16.
 */
public class Noise {
    private double values[];
    private int width;
    private int height;

    public Noise(int width, int height) {
        values = new double[width * height];
        this.width = width;
        this.height = height;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                values[i + j * width] = Math.random();
            }
        }
    }

    public double get(int x, int y) {
        return values[x + y * width];
    }

    public double turbulence(double x, double y, double size) {
        double value = 0;
        double initialSize = size;

        while (size >= 1) {
            value += getSmooth(x / size, y / size) * size;
            size /= 2.0;
        }

        return value / (initialSize * 2);
    }

    public double getSmooth(double x, double y) {
        double fractX = x - (int)(x);
        double fractY = y - (int)(y);

        int x1 = ((int)x + width) % width;
        int y1 = ((int)y + height) % height;

        int x2 = (x1 + width - 1) % width;
        int y2 = (y1 + height -1) % height;

        double value = 0;
        value += fractX * fractY * get(x1, y1);
        value += (1 - fractX) * fractY * get(x2, y1);
        value += fractX * (1 - fractY) * get(x1, y2);
        value += (1 - fractX) * (1 - fractY) * get(x2, y2);

        return value;
    }
}
