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
        clamp();
    }

    public Color sum(Color other) {
        if (other == null) {
            return this;
        }
    	return new Color(red + other.red, green + other.green, blue + other.blue);
    }
    
    private void clamp() {
    	if (red < 0) {
    		red = 0;
    	} else if (red > 1) {
    		red = 1;
    	}
    	
    	if (green < 0) {
    		green = 0;
    	} else if (green > 1) {
    		green = 1;
    	}
    	
    	if (blue < 0) {
    		blue = 0;
    	} else if (blue > 1) {
    		blue = 1;
    	}
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
