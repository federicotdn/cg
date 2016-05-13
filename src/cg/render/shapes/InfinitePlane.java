package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

/**
 * Created by Hobbit on 4/14/16.
 */
public class InfinitePlane extends Primitive {
    public static final Vec3 PLANE_NORMAL = new Vec3(0, 1, 0);

    public InfinitePlane(Vec3 t, Vec3 r, Vec3 s) {
    	setTransform(t, r, s);
    }
    
    @Override
    protected Collision calculateCollision(Ray ray) {
        Double t = planeT(ray, PLANE_NORMAL, 0);
        if (t == null || t > ray.getMaxT()) {
        	return null;
        }
        Vec3 colPos = ray.runDistance(t);
        return new Collision(this, ray, t, PLANE_NORMAL, colPos.x/10, colPos.z/10);
    }

    public static Double planeT(Ray ray, Vec3 normal, double d) {
        Vec3 rDir = ray.getDirection();
        Vec3 rPos = ray.getOrigin();
        double collide = rDir.dot(normal);

        if (Math.abs(collide) > 0) {
            double t = -((rPos.dot(normal) + d)/rDir.dot(normal));
            if (t > 0 && t <= ray.getMaxT()) {
                return t;
            }
        }

        return null;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return null;
    }
}
