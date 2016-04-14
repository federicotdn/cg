package cg.render;

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
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color sum(Color other) {
        if (other == null) {
            return this;
        }

    	float r = (red + other.red > 1 ? 1 : red + other.red);
    	float g = (green + other.green > 1 ? 1 : green + other.green);
    	float b = (blue + other.blue > 1 ? 1 : blue + other.blue);
    	return new Color(r, g, b);
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
