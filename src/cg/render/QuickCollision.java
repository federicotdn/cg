package cg.render;

import cg.math.Vec3;

public class QuickCollision {
	private final Primitive primitive;
	private final Ray localRay;
	private final Ray worldRay;
	private final double localT;
	private final double worldT;
	private Vec3 localPosition;
	
	public QuickCollision(Primitive primitive, Ray localRay, Ray worldRay, double localT, double worldT) {
		this.primitive = primitive;
		this.localRay = localRay;
		this.worldRay = worldRay;
		this.localT = localT;
		this.worldT = worldT;
		this.localPosition = null;
	}
	
	public Vec3 getLocalPosition() {
		if (localPosition == null) {
			localPosition = localRay.runDistance(localT);
		}
		
		return localPosition;
	}
	
	public Collision completeCollision() {
		return primitive.completeCollision(this);
	}
	
	public double getLocalT() {
		return localT;
	}
}
