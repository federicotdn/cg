package cg.math;

/**
 * Created by Hobbit on 4/17/16.
 */
public class MathUtils {
    public static float clamp(float x, float min, float max) {
        if (x < min) {
            return min;
        }

        if (x > max) {
            return max;
        }

        return x;
    }

    public static float clamp(float x) {
        return clamp(x, 0, 1);
    }
}
