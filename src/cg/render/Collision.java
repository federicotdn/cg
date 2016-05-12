package cg.render;

import cg.math.Vec3;

public class Collision {
	private float t;
	private final Ray ray;
	private final Vec3 normal;
	private Vec3 position;
	private final float cosAngle;
	
	private final Primitive primitive;
	public final float u;
	public final float v;
	
	public Collision(Primitive primitive, Ray ray, float t, Vec3 normal, float u, float v) {
		this.ray = ray;
		this.t = t;
		this.normal = normal.normalize();
		this.position = null;
		this.primitive = primitive;
		this.u = u;
		this.v = v;
		
		this.cosAngle = ray.getDirection().mul(-1).dot(this.normal);
	}
	
	public Vec3 getPosition() {
		if (position == null) {
			position = ray.runDistance(t);
		}
		
		return position;
	}

	public Ray getRay() {
		return ray;
	}
	
	public Primitive getPrimitive() {
		return primitive;
	}
	
	public float getCosAngle() {
		return cosAngle;
	}
	
	public Vec3 getNormal() {
		return normal;
	}
	
	public float getT() {
		return t;
	}
}
