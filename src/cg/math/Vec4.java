package cg.math;

public class Vec4 {
	/*
	 * 4x1 Vector
	 * 
	 * -----
	 * | x |
	 * |   |
	 * | y |
	 * |   |
	 * | z |
	 * |   |
	 * | w |
	 * -----
	 */	

	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4() {
		/* float fields initialized to 0.0f by default */
	}
	
	public Vec4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	// M x V (stores result in V)
	public Vec4 mul(Matrix4 m) {
		float n0 = x * m.m00 + y * m.m01 + z * m.m02 + w * m.m03;
		float n1 = x * m.m10 + y * m.m11 + z * m.m12 + w * m.m13;
		float n2 = x * m.m20 + y * m.m21 + z * m.m22 + w * m.m23;
		float n3 = x * m.m30 + y * m.m31 + z * m.m32 + w * m.m33;

		x = n0;
		y = n1;
		z = n2;
		w = n3;
		
		return this;
	}
}
