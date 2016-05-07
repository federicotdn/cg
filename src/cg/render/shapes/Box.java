package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
import cg.render.Ray;

public class Box extends Primitive {
	private final Vec3 pMin;
	private final Vec3 pMax;
	private float width;
	private float height;
	private float depth;
	
	public Box(float width, float height, float depth, Vec3 t, Vec3 r, Vec3 s) {
		pMax = new Vec3(width / 2, height / 2, depth / 2);
		pMin = new Vec3(-width / 2, -height / 2, -depth / 2);
		this.width = width;
		this.height = height;
		this.depth = depth;
		setTransform(t, r, s);
	}
	
	@Override
//<<<<<<< ffb6a8f7bb8a60ac6ae617e2953add56f0e3c9d3
//	protected Collision calculateCollision(Ray ray) {
//		float t = collisionForBox(pMin, pMax, ray);
//		if (t < 0) {
//=======
	protected QuickCollision calculateCollision(Ray ray) {
		float t = collisionForBox(pMin, pMax, ray);
		if (t < 0) {
			return null;
		}

		return new QuickCollision(this, ray, t, -1);
//		Vec3 colPos = ray.runDistance(t);
//		Vec3 normal;
//		float maxDist;
//		float distance;
//		float u, v;
//		
//		//X
//		distance = Math.abs(colPos.x)/(width/2);
//		maxDist = distance;
//		normal = new Vec3(Math.signum(colPos.x), 0, 0);
//		v = Math.abs(colPos.y - height / 2) / height;
//		u = Math.abs(colPos.z - depth / 2) / depth;
//
//		
//		//Y
//		distance = Math.abs(colPos.y)/(height/2);
//		if (distance > maxDist) {
//			maxDist = distance;
//			normal = new Vec3(0, Math.signum(colPos.y), 0);
//			u = Math.abs(colPos.x - width/2)/width;
//			v = Math.abs(colPos.z - depth/2)/depth;
//		}		
//		
//		//Z
//		distance = Math.abs(colPos.z)/(depth/2);
//		if (distance > maxDist) {
//			maxDist = distance;
//			normal = new Vec3(0, 0, Math.signum(colPos.z));
//			u = Math.abs(colPos.x - width/2)/width;
//			v = Math.abs(colPos.y - height/2)/height;
//		}
	}
	
    public static float collisionForBox(Vec3 pMin, Vec3 pMax, Ray ray) {
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
        if (t0 > t1) return -1;

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
        if (t0 > t1) return -1;

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
        if (t0 > t1) return -1;

        return t0;
    }

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return (new BoundingBox(pMin, pMax)).transformBBox(trs);
	}

	@Override
	public Collision completeCollision(QuickCollision qc) {
		// TODO Auto-generated method stub
		return null;
	}
}
