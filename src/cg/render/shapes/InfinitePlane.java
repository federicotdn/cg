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

    @Override
    protected Collision calculateCollision(Ray ray) {
        Float t = planeT(ray, PLANE_NORMAL, 0);
        if (t == null || t > ray.getMaxT()) {
        	return null;
        }
        
        return new Collision(this, ray, t, PLANE_NORMAL, 0, 0);
    }

    public static Float planeT(Ray ray, Vec3 normal, float d) {
        Vec3 rDir = ray.getDirection();
        Vec3 rPos = ray.getOrigin();
        float collide = rDir.dot(normal);

        if (Math.abs(collide) > 0) {
            float t = -((rPos.dot(normal) + d)/rDir.dot(normal));
            if (t > 0) {
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
