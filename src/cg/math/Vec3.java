package cg.math;

public class Vec3 {
	/*
	 * 3x1 Vector
	 * 
	 * ------
	 * | v0 |
	 * |    |
	 * | v1 |
	 * |    |
	 * | v2 |
	 * ------
	 */	

	public float v0;
	public float v1;
	public float v2;
	private boolean normalized;
	
	public Vec3() {
		/* float fields initialized to 0.0f by default */
	}
	
	public Vec3(float v0, float v1, float v2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public Vec3 nor() {
		if (!normalized) {
			float len = (float) Math.sqrt(v0 * v0 + v1 * v1 + v2 * v2);
			v0 /= len;
			v1 /= len;
			v2 /= len;
			normalized = true;
		}
		return this;
	}
}
