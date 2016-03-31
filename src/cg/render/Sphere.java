package cg.render;

import java.util.Optional;

import cg.math.Vec3;

public class Sphere extends Primitive {

	private float radius;
	
	public Sphere(float radius) {
		this.radius = radius;
	}
	
	@Override
	protected Optional<Collision> calculateCollision(Ray ray) {
		Vec3 orig = ray.getOrigin();
		Vec3 dir = ray.getDirection();
		
		float A = (dir.x * dir.x) + (dir.y * dir.y) + (dir.z * dir.z);
		float B = 2 * (dir.x * orig.x + dir.y * orig.y + dir.z * orig.z);
		float C = (orig.x * orig.x) + (orig.y * orig.y) + (orig.z * orig.z) - (radius * radius);
		
		float det = (B * B) - (4 * A * C);
		if (det < 0) {
			return Optional.empty();
		}
		
		float t0 = (float) ((-B - Math.sqrt(det)) / (2 * A));
		float t1 = (float) ((-B + Math.sqrt(det)) / (2 * A));
		
		float t = (t0 > t1 ? t1 : t0);
		
		return Optional.of(new Collision(ray, t));
	}
}
