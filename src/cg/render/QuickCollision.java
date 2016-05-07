package cg.render;

public class QuickCollision {
	private final float localT;
	private final float worldT;
	private final Ray ray;
	private final Primitive primitive;
	
	//Mesh only:
	int faceIndex;
	float b1;
	float b2;
	
	public QuickCollision(Primitive primitive, Ray ray, float localT, float worldT) {
		this.localT = localT;
		this.ray = ray;
		this.primitive = primitive;
		this.worldT = worldT;
	}
	
	public float getLocalT() {
		return localT;
	}
	
	public float getWorldT() {
		return worldT;
	}
}
