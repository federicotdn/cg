package cg.math;

public class MathUtils {
    public static double EPSILON = 0.0000000001;

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
    
    public static Vec3 squareToHemisphere(double x, double y, double e) {
        double phi = 2 * Math.PI * x;
        double cosphi = Math.cos(phi);
        double sinphi = Math.sin(phi);

        double costheta = Math.pow(1 - y, 1.0/(e + 1));
    	double sintheta = Math.sqrt(1 - (costheta * costheta));

    	double pu = sintheta * cosphi;
    	double pv = sintheta * sinphi;
        double pw = costheta;
    	return new Vec3(pu, pw, pv);
    }

    public static Vec3 tangentToWorldSpace(Vec3 v, Vec3 dir) {
        Vec3 tan = dir.getSmallestAxis().cross(dir).normalize();
        Vec3 bitan = tan.cross(dir).normalize();
        Vec3 newDir = new Vec3(v.x * tan.x + v.y * dir.x + v.z * bitan.x,
                v.x * tan.y + v.y * dir.y + v.z * bitan.y,
                v.x * tan.z + v.y * dir.z + v.z * bitan.z).normalize();
        return newDir;
    }
}
