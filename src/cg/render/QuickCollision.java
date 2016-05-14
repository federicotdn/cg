package cg.render;

import cg.math.Vec3;

public class QuickCollision {
	private final Primitive primitive;
	private final Ray localRay;
	private final Ray worldRay;
	private final double localT;
	private final double worldT;
	private Vec3 localPosition;
	
	//Mesh only
	private int faceIndex;
	private double b1;
	private double b2;
	
	public QuickCollision(Primitive primitive, Ray localRay, Ray worldRay, double localT, double worldT) {
		this.primitive = primitive;
		this.localRay = localRay;
		this.worldRay = worldRay;
		this.localT = localT;
		this.worldT = worldT;
		this.localPosition = null;
	}
	
	public void setMeshData(int faceIndex, double b1, double b2) {
		this.faceIndex = faceIndex;
		this.b1 = b1;
		this.b2 = b2;
	}
	
	public void copyMeshData(QuickCollision other) {
		this.faceIndex = other.faceIndex;
		this.b1 = other.b1;
		this.b2 = other.b2;
	}
	
	public int getFaceIndex() {
		return faceIndex;
	}
	
	public double getB1() {
		return b1;
	}
	
	public double getB2() {
		return b2;
	}
	
	public Vec3 getLocalPosition() {
		if (localPosition == null) {
			localPosition = localRay.runDistance(localT);
		}
		
		return localPosition;
	}
	
	public Ray getLocalRay() {
		return localRay;
	}
	
	public Ray getWorldRay() {
		return worldRay;
	}
	
	public Collision completeCollision() {
		return primitive.completeCollision(this);
	}
	
	public double getLocalT() {
		return localT;
	}
	
	public double getWorldT() {
		return worldT;
	}
	
	public Primitive getPrimitive() {
		return primitive;
	}
}
