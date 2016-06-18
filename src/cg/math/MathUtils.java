package cg.math;

public class MathUtils {
    public static double clamp(double x, double min, double max) {
        if (x < min) {
            return min;
        }

        if (x > max) {
            return max;
        }

        return x;
    }

    public static double clamp(double x) {
        return clamp(x, 0, 1);
    }
    
    public static Vec3 squareToHemisphere(double x, double y) {
    	double e2 = x;
    	double e1 = y;
    	
    	double twoPiE2 = 2 * Math.PI * e2;
    	double sqrtOneE1 = Math.sqrt(1 - (e1 * e1));
    	
    	x = Math.cos(twoPiE2) * sqrtOneE1;
    	y = Math.sin(twoPiE2) * sqrtOneE1;
    	return new Vec3(x, y, e1);
    }
}
