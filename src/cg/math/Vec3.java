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
	
	public Vec3() {
		/* float fields initialized to 0.0f by default */
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3 normalize() {
		float len = (float) Math.sqrt(x * x + y * y + z * z);
		x /= len;
		y /= len;
		z /= len;
		return this;
	}
}
