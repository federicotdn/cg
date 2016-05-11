package cg.render;

import cg.math.Vec3;

public class Ray {
	public final static float DEFAULT_MAX_T = Float.POSITIVE_INFINITY;
	private final Vec3 origin;
	private final Vec3 direction;
	private final float maxT;
	
	public Ray(Vec3 origin, Vec3 direction, Float maxT) {
		this.origin = origin;
		this.direction = direction.normalize();
		
		if (maxT != null) {
			this.maxT = maxT;
		} else {
			this.maxT = DEFAULT_MAX_T;
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
	
	public Vec3 runDistance(float t) {
		return origin.sum(direction.mul(t));
	}
}
