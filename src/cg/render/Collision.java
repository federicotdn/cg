package cg.render;

import cg.math.Vec3;

public class Collision {
	private float t;
	private Ray ray;
	private Vec3 normal;
	private Vec3 position;
	private float cosAngle;
	
	public String shapeName;
	public Collision setName(String s) {
		shapeName = s;
		return this;
	}
	
	public Collision(Ray ray, float t, Vec3 normal) {
		this.ray = ray;
		this.t = t;
		this.normal = normal.normalize();
		this.position = null;
		
		this.cosAngle = ray.getDirection().mul(-1).dot(this.normal);
	}
	
	public Vec3 getPosition() {
		if (position == null) {
			position = ray.getOrigin().sum(ray.getDirection().mul(t));
		}
		
		return position;
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
