package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
import cg.render.Ray;

public class Sphere extends Primitive {

	private double radius;
	
	public Sphere(Vec3 t, Vec3 r, Vec3 s, double radius) {
		this.radius = radius;
		setTransform(t, r, s);
	}

	@Override
	public BoundingBox calculateBBox(Matrix4 trs) {
		Vec3 pMin = new Vec3(-radius, -radius, -radius);
		Vec3 pMax = pMin.mul(-1);

		return new BoundingBox(pMin, pMax).transformBBox(trs);
	}

	@Override
	public QuickCollision internalQuickCollideWith(Ray ray) {
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();
		
		double A = (dir.x * dir.x) + (dir.y * dir.y) + (dir.z * dir.z);
		double B = 2 * (dir.x * orig.x + dir.y * orig.y + dir.z * orig.z);
		double C = (orig.x * orig.x) + (orig.y * orig.y) + (orig.z * orig.z) - (radius * radius);
		
		double disc = (B * B) - (4 * A * C);
		if (disc <= 0) {
			return null;
		}

		double rootDisc = Math.sqrt(disc);
		double q;
		if (B < 0) {
			q = -0.5 *(B - rootDisc);
		} else {
			q = -0.5 *(B + rootDisc);
		}

		double t0 = q/A;
		double t1 = C/q;

		double t = (t0 > t1 ? t1 : t0);

		if (t0 > ray.getMaxT() || t1 < 0) {
			return null;
		}

		if (t0 < 0 && t1 > ray.getMaxT()) {
			return null;
		}


		return new QuickCollision(this, ray, null, t, -1);
	}

	@Override
	public Collision internalCompleteCollision(QuickCollision qc) {
		Ray ray = qc.getLocalRay();
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();
		double t = qc.getLocalT();
		
		Vec3 normal = orig.sum(dir.mul(t)).mul(1/radius);

		double u = 0.5 + ((Math.atan2(normal.z, normal.x))/(2*Math.PI));
		double v = 0.5 - (Math.asin(normal.y)/Math.PI);


		return new Collision(this, ray, t, normal, u, v);
	}
}
