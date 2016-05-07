package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
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
//<<<<<<< ffb6a8f7bb8a60ac6ae617e2953add56f0e3c9d3
//    protected Collision calculateCollision(Ray ray) {
//        float t = planeT(ray, PLANE_NORMAL, 0);
//        if (t < 0 || t > ray.getMaxT()) {
//        	return null;
//        }
//        Vec3 colPos = ray.runDistance(t);
//        return new Collision(this, ray, t, PLANE_NORMAL, colPos.x / 10, colPos.z / 10);
//=======
    protected QuickCollision calculateCollision(Ray ray) {
        float t = planeT(ray, PLANE_NORMAL, 0);
        if (t < 0 || t > ray.getMaxT()) {
        	return null;
        }

        return new QuickCollision(this, ray, null, t, -1);
    }

    public static float planeT(Ray ray, Vec3 normal, float d) {
        Vec3 rDir = ray.getDirection();
        Vec3 rPos = ray.getOrigin();
        float collide = rDir.dot(normal);

        if (Math.abs(collide) > 0) {
            float t = -((rPos.dot(normal) + d) / rDir.dot(normal));
            if (t > 0 && t <= ray.getMaxT()) {
                return t;
            }
        }

        return -1;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return null;
    }

	@Override
	public Collision getFullCollision(QuickCollision qc) {
		Ray ray = qc.getLocalRay();
		float t = qc.getLocalT();
		Vec3 colPos = ray.runDistance(t);
		return new Collision(this, ray, t, PLANE_NORMAL, colPos.x / 10, colPos.z / 10);
	}
}
