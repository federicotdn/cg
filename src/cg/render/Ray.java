package cg.render;

import cg.math.Vec3;

public class Ray {
	public final static double DEFAULT_MAX_T = Double.POSITIVE_INFINITY;
	private Vec3 origin;
	private Vec3 direction;
	private int hops;
	private double maxT = DEFAULT_MAX_T;
	private boolean insidePrimitive;
	private boolean ignoreShadows;


	//TODO: Change Double to double
	public Ray(Vec3 origin, Vec3 direction, Double maxT) {
		this(origin, direction, maxT, 1);
	}

	//TODO: Change Double to double
	public Ray(Vec3 origin, Vec3 direction, Double maxT, int hops) {
		this(origin, direction, maxT, hops, false, false);
	}

	//TODO: Change Double to double
	public Ray(Vec3 origin, Vec3 direction, Double maxT, int hops, boolean insidePrimitive, boolean ignoreShadows) {
		this.origin = origin;
		this.direction = direction.normalize();
		this.hops = hops;
		this.insidePrimitive = insidePrimitive;
		this.ignoreShadows = ignoreShadows;

		if (maxT != null) {
			this.maxT = maxT;
		}
	}

	public double getMaxT() {
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

	public boolean shouldIgnoreShadows() {
		return ignoreShadows;
	}

	public Vec3 runDistance(double t) {
		return origin.sum(direction.mul(t));
	}
}
