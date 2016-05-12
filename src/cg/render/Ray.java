package cg.render;

import cg.math.Vec3;

public class Ray {
	public final static float DEFAULT_MAX_T = Float.POSITIVE_INFINITY;
	private Vec3 origin;
	private Vec3 direction;
	private int hops;
	private float maxT = DEFAULT_MAX_T;
	private boolean insidePrimitive;
	
	public Ray(Vec3 origin, Vec3 direction, Float maxT) {
		this(origin, direction, maxT, 1);
	}

	public Ray(Vec3 origin, Vec3 direction, Float maxT, int hops) {
		this(origin, direction, maxT, hops, false);
	}

	public Ray(Vec3 origin, Vec3 direction, Float maxT, int hops, boolean insidePrimitive) {
		this.origin = origin;
		this.direction = direction.normalize();
		this.hops = hops;
		this.insidePrimitive = insidePrimitive;

		if (maxT != null) {
			this.maxT = maxT;
		}
	}

	public float getMaxT() {
		return maxT;
	}

	public int getHops() {
		return hops;
	}

	public boolean isInsidePrimitive() {
		return insidePrimitive;
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
