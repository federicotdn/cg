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

    public static Vec2 squareToDisk(double x, double y) {
        double sx = (2.0 * x) - 1;
        double sy = (2.0 * y) - 1;

        double phi, r;
        if (sx > - sy) {
            if (sx > sy) {
                r = sx;
                phi = sy/sx;
            } else {
                r = sy;
                phi =  2 - (sx/sy);
            }
        } else {
            if (sx < sy) {
                r = -sx;
                phi = 4 + (sy/sx);
            } else {
                r = -sy;
                if (sy != 0) {
                    phi = 6 - (sx/sy);
                } else {
                    phi = 0;
                }
            }
        }

        phi *= Math.PI/4;
        return new Vec2(r * Math.cos(phi), r * Math.sin(phi));
    }

    public static Vec3 tangentToWorldSpace(Vec3 v, Vec3 dir) {
        Vec3 tan = dir.getSmallestAxis().cross(dir).normalize();
        Vec3 bitan = tan.cross(dir).normalize();
        Vec3 newDir = new Vec3(v.x * tan.x + v.y * dir.x + v.z * bitan.x,
                v.x * tan.y + v.y * dir.y + v.z * bitan.y,
                v.x * tan.z + v.y * dir.z + v.z * bitan.z).normalize();
        return newDir;
    }

    public static double schlick(double n1, double n2, double cosI, double sen2t) {
        double r;
        double r0 = Math.pow((n1 - n2) / (n1 + n2), 2);
        double cos;
        if (n1 <= n2) {
            cos = cosI;
        } else {
            if (sen2t > 1) {
                return 1;
            }
            cos = Math.sqrt(1 - sen2t);
        }

        r = r0 + ((1 - r0) * (Math.pow(1 - cos, 5)));

        return MathUtils.clamp(r);
    }

    public static double schlick(double n, double cosI) {
        double r0 = Math.pow((1 - n) / (1 + n), 2);

        double r = r0 + ((1 - r0) * (Math.pow(1 - cosI, 5)));

        return MathUtils.clamp(r);
    }
}
