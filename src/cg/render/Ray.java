package cg.render;

import cg.math.Vec3;

public class Ray {
	private Vec3 origin;
	private Vec3 direction;
	
	public Ray(Vec3 origin, Vec3 direction) {
		this.origin = origin;
		this.direction = direction.normalize();
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	public Vec3 getDirection() {
		return direction;
	}
}
