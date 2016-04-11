package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

public class Sphere extends Primitive {

	private float radius;
	
	public Sphere(Vec3 t, Vec3 r, Vec3 s, float radius) {
		this.radius = radius;
		setTransform(t, r, s);
	}
	
	@Override
	protected Collision calculateCollision(Ray ray) {
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();
		
		float A = (dir.x * dir.x) + (dir.y * dir.y) + (dir.z * dir.z);
		float B = 2 * (dir.x * orig.x + dir.y * orig.y + dir.z * orig.z);
		float C = (orig.x * orig.x) + (orig.y * orig.y) + (orig.z * orig.z) - (radius * radius);
		
		float det = (B * B) - (4 * A * C);
		if (det < 0) {
			return null;
		}
		
		float t0 = (float) ((-B - Math.sqrt(det)) / (2 * A));
		float t1 = (float) ((-B + Math.sqrt(det)) / (2 * A));
		
		float t = (t0 > t1 ? t1 : t0);
		
		return new Collision(ray, t);
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 t, Matrix4 r, Matrix4 s) {
		Vec3 pMin = new Vec3(-radius, -radius, -radius);
		Vec3 pMax = pMin.mul(-1);
		Matrix4 ts = t.mult(s);

		pMin = ts.mul(pMin.asPosition()).toVec3();
		pMax = ts.mul(pMax.asPosition()).toVec3();

		return  new BoundingBox(pMin, pMax);
	}
}
