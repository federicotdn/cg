package cg.render;

/**
 * Created by Hobbit on 4/6/16.
 */
public class Color {
    private double alpha;
    private double red;
    private double green;
    private double blue;

    public static final Color RED = new Color(1,0,0);
    public static final Color GREEN = new Color(0,1,0);
    public static final Color BLUE = new Color(0,0,1);
    public static final Color BLACK = new Color(0);
    public static final Color WHITE = new Color(1);

    public Color(double red, double green, double blue) {
        this(1.0f, red, green, blue);
    }

    public Color(double color) {
        this(1.0f, color, color, color);
    }

    public Color(double alpha, double red, double green, double blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
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

    public Color mul(double v) {
    	return new Color(red * v, green * v, blue * v);
    }

    public double getAlpha() {
        return alpha;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
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
