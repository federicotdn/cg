package cg.math;

/**
 * Created by Hobbit on 4/17/16.
 */
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
}
