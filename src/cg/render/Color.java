package cg.render;

/**
 * Created by Hobbit on 4/6/16.
 */
public class Color {
    private float alpha;
    private float red;
    private float blue;
    private float green;

    public static final Color RED = new Color(1,0,0);
    public static final Color GREEN = new Color(0,1,0);
    public static final Color BLUE = new Color(0,0,1);
    public static final Color BLACK = new Color(0);
    public static final Color WHITE = new Color(1);

    public Color(float red, float blue, float green) {
        this(1.0f, red, blue, green);

    }

    public Color(float color) {
        this(1.0f, color, color, color);
    }

    public Color(float alpha, float red, float blue, float green) {
        this.alpha = alpha;
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getRed() {
        return red;
    }

    public float getBlue() {
        return blue;
    }

    public float getGreen() {
        return green;
    }
}
