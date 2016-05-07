package cg.render;

public class QuickCollision {
	private final float localT;
	private final float worldT;
	private final Ray localRay;
	private final Ray worldRay;
	private final Primitive primitive;
	
	//Mesh only:
	int faceIndex;
	float b1;
	float b2;

	public QuickCollision(Primitive primitive, Ray localRay, Ray worldRay, float localT, float worldT) {
		this.localT = localT;
		this.localRay = localRay;
		this.worldRay = worldRay;
		this.primitive = primitive;
		this.worldT = worldT;
	}
	
	public void setMeshData(int faceIndex, float b1, float b2) {
		this.faceIndex = faceIndex;
		this.b1 = b1;
		this.b2 = b2;
	}
	
	public float getLocalT() {
		return localT;
	}
	
	public float getWorldT() {
		return worldT;
	}
	
	public Ray getLocalRay() {
		return localRay;
	}
	
	public Ray getWorldRay() {
		return worldRay;
	}
	
	public Primitive getPrimitive() {
		return primitive;
	}
	
	public Collision completeCollision() {
		return primitive.getFullCollision(this);
	}
}
