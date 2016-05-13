package cg.render;

import cg.math.Vec3;

public class Collision {
	private double t;
	private final Ray ray;
	private final Vec3 normal;
	private Vec3 position;
	private final double cosAngle;
	
	private final Primitive primitive;
	public final double u;
	public final double v;
	
	public Collision(Primitive primitive, Ray ray, double t, Vec3 normal, double u, double v) {
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
	
	public double getCosAngle() {
		return cosAngle;
	}
	
	public Vec3 getNormal() {
		return normal;
	}
	
	public double getT() {
		return t;
	}
}
