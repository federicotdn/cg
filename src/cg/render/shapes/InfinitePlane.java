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
    private static final Vec3 NORMAL = new Vec3(0, 1, 0);

    @Override
    protected Collision calculateCollision(Ray ray) {
        Vec3 rDir = ray.getDirection();
        Vec3 rPos = ray.getOrigin();
        float collide = rDir.dot(NORMAL);

        if (Math.abs(collide) > 0) {
            float t = -(rPos.dot(NORMAL)/rDir.dot(NORMAL));
            if (t > 0) {
                return new Collision(this, ray, t, NORMAL, 0, 0);
            }
        }

        return null;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return null;
    }
}
