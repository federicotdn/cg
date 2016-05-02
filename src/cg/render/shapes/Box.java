package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

public class Box extends Primitive {
	private final Vec3 pMin;
	private final Vec3 pMax;
	
	public Box(float width, float height, float depth, Vec3 t, Vec3 r, Vec3 s) {
		pMax = new Vec3(width / 2, height / 2, depth / 2);
		pMin = new Vec3(-width / 2, -height / 2, -depth / 2);
		setTransform(t, r, s);
	}
	
	@Override
	protected Collision calculateCollision(Ray ray) {
		Float t = collisionForBox(pMin, pMax, ray);
		if (t == null) {
			return null;
		}
		
		//TODO: Fix normal when width, depth or height != 1
		Vec3 colPos = ray.runDistance(t);
		Vec3 normal = null;
		float maxDist = 0;
		float distance;
		
		//X
		distance = Math.abs(colPos.x);
		if (distance > maxDist) {
			maxDist = distance;
			normal = new Vec3(Math.signum(colPos.x), 0, 0);
		}
		
		//Y
		distance = Math.abs(colPos.y);
		if (distance > maxDist) {
			maxDist = distance;
			normal = new Vec3(0, Math.signum(colPos.y), 0);
		}		
		
		//Z
		distance = Math.abs(colPos.z);
		if (distance > maxDist) {
			maxDist = distance;
			normal = new Vec3(0, 0, Math.signum(colPos.z));
		}

		return new Collision(this, ray, t, normal, 0.0f, 0.0f);
	}
	
    public static Float collisionForBox(Vec3 pMin, Vec3 pMax, Ray ray) {
        float t0 = 0f, t1 = ray.getMaxT();
        float invRayDir = 1.f / ray.getDirection().x;
        float tNear = (pMin.x  - ray.getOrigin().x) * invRayDir;
        float tFar =  (pMax.x  - ray.getOrigin().x) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        invRayDir = 1.f / ray.getDirection().y;
        tNear = (pMin.y  - ray.getOrigin().y) * invRayDir;
        tFar =  (pMax.y  - ray.getOrigin().y) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        invRayDir = 1.f / ray.getDirection().z;
        tNear = (pMin.z  - ray.getOrigin().z) * invRayDir;
        tFar =  (pMax.z  - ray.getOrigin().z) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        return t0;
    }

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return (new BoundingBox(pMin, pMax)).transformBBox(trs);
	}
}
