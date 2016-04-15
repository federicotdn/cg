package cg.render;

import cg.math.Vec3;

public class Ray {
	public final static float DEFAULT_MAX_T = Float.POSITIVE_INFINITY;
	private Vec3 origin;
	private Vec3 direction;
	private float maxT = DEFAULT_MAX_T;
	
	public Ray(Vec3 origin, Vec3 direction, Float maxT) {
		this.origin = origin;
		this.direction = direction.normalize();
		
		if (maxT != null) {
			this.maxT = maxT;
		}
	}
	
	public float getMaxT() {
		return maxT;
	}
	
	public Vec3 getOrigin() {
		return origin;
	}
	
	public Vec3 getDirection() {
		return direction;
	}
}
