package cg.math;

public class Vec3 {
	/*
	 * 3x1 Vector
	 * 
	 * -----
	 * | x |
	 * |   |
	 * | y |
	 * |   |
	 * | z |
	 * -----
	 */	

	public float x;
	public float y;
	public float z;
	private boolean normalized;
	
	public Vec3() {
		/* float fields initialized to 0.0f by default */
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3 normalize() {
		if (!normalized) {
			float len = (float) Math.sqrt(x * x + y * y + z * z);
			x /= len;
			y /= len;
			z /= len;
			normalized = true;
		}
		return this;
	}
}
