package cg.render.shapes;

import cg.math.MathUtils;
import cg.math.Matrix4;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.render.*;

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

	public static double sphereCollisionT(Ray ray, double radius) {
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();

		double A = (dir.x * dir.x) + (dir.y * dir.y) + (dir.z * dir.z);
		double B = 2 * (dir.x * orig.x + dir.y * orig.y + dir.z * orig.z);
		double C = (orig.x * orig.x) + (orig.y * orig.y) + (orig.z * orig.z) - (radius * radius);

		double disc = (B * B) - (4 * A * C);
		if (disc <= 0) {
			return -1;
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
			return -1;
		}

		if (t0 < 0 && t1 > ray.getMaxT()) {
			return -1;
		}

		return t;
	}

	@Override
	public QuickCollision internalQuickCollideWith(Ray ray) {
		double t = sphereCollisionT(ray, radius);
		if (t < 0) {
			return null;
		}

		return new QuickCollision(this, ray, null, t, -1);
	}

	public static Vec2 uvs(Vec3 normal) {
		double u = 0.5 + ((Math.atan2(normal.z, normal.x))/(2*Math.PI));
		double v = 0.5 - (Math.asin(normal.y)/Math.PI);

		return new Vec2(u, v);
	}

	@Override
	public Collision internalCompleteCollision(QuickCollision qc) {
		Ray ray = qc.getLocalRay();
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();
		double t = qc.getLocalT();
		
		Vec3 normal = orig.sum(dir.mul(t)).mul(1/radius);

		Vec2 uvs = uvs(normal);

		if (getMaterial().hasNormalMap()) {
			Vec3 auxN = new Vec3(normal.x, 0, normal.z);

			Vec3 tan = new Vec3(-normal.z, 0, normal.x);
			tan = tan.cross(auxN).normalize();
			Vec3 bitan = tan.cross(normal).normalize();

			Vec3 mapNormal = getMaterial().getNormal(uvs.x, uvs.y);
			normal = MathUtils.tbn(tan, bitan, normal, mapNormal);
		}

		return new Collision(this, ray, t, normal, uvs.x, uvs.y);
	}

	public double getRadius() {
		return radius;
	}
}
