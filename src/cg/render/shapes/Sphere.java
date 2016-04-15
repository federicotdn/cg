package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

public class Sphere extends Primitive {

	private float radius;
	public int id;
	private static int idgen = 0;
	
	public Sphere(Vec3 t, Vec3 r, Vec3 s, float radius) {
		this.radius = radius;
		setTransform(t, r, s);
		id = idgen++;
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
		Vec3 normal = orig.sum(dir.mul(t));
		
		return new Collision(ray, t, normal).setName("sphere" + String.valueOf(id));
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		Vec3 pMin = new Vec3(-radius, -radius, -radius);
		Vec3 pMax = pMin.mul(-1);

		return  new BoundingBox(pMin, pMax).calculateBBox(trs);
	}
}
