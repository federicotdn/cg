package cg.render;

import cg.math.MathUtils;

/**
 * Created by Hobbit on 4/6/16.
 */
public class Color {
    private float alpha;
    private float red;
    private float green;
    private float blue;

    public static final Color RED = new Color(1,0,0);
    public static final Color GREEN = new Color(0,1,0);
    public static final Color BLUE = new Color(0,0,1);
    public static final Color BLACK = new Color(0);
    public static final Color WHITE = new Color(1);

    public Color(float red, float green, float blue) {
        this(1.0f, red, green, blue);
    }

    public Color(float color) {
        this(1.0f, color, color, color);
    }

    public Color(float alpha, float red, float green, float blue) {
        this.alpha = MathUtils.clamp(alpha);
        this.red = MathUtils.clamp(red);
        this.green = MathUtils.clamp(green);
        this.blue = MathUtils.clamp(blue);
    }

    public Color sum(Color other) {
        if (other == null) {
            return this;
        }
    	return new Color(red + other.red, green + other.green, blue + other.blue);
    }

    public Color mul(Color color) {
        return new Color(color.alpha * alpha, color.red * red, color.green * green, color.blue * blue);
    }

    public Color mul(float v) {
    	return new Color(red * v, green * v, blue * v);
    }

    public float getAlpha() {
        return alpha;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    @Override
    public String toString() {
        return "Color{" +
                "alpha=" + alpha +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
