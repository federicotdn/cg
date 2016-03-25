package cg.math;

public class Vec4 {
	/*
	 * 4x1 Vector
	 * 
	 * ------
	 * | v0 |
	 * |    |
	 * | v1 |
	 * |    |
	 * | v2 |
	 * |    |
	 * | v3 |
	 * ------
	 */	

	public float v0;
	public float v1;
	public float v2;
	public float v3;
	
	public Vec4() {
		/* float fields initialized to 0.0f by default */
	}
	
	public Vec4(float v0, float v1, float v2, float v3) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	// M x V (stores result in V)
	public Vec4 mul(Matrix4 m) {
		float n0 = v0 * m.m00 + v1 * m.m01 + v2 * m.m02 + v3 * m.m03;
		float n1 = v0 * m.m10 + v1 * m.m11 + v2 * m.m12 + v3 * m.m13;
		float n2 = v0 * m.m20 + v1 * m.m21 + v2 * m.m22 + v3 * m.m23;
		float n3 = v0 * m.m30 + v1 * m.m31 + v2 * m.m32 + v3 * m.m33;

		v0 = n0;
		v1 = n1;
		v2 = n2;
		v3 = n3;
		
		return this;
	}
}
