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

	public final float x;
	public final float y;
	public final float z;
	public final float w;
	
	private Vec4() {
		/* float fields initialized to 0.0f by default */
		this(0, 0, 0, 0);
	}
	
	public Vec4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4 normalize() {
		float len = (float) Math.sqrt(x * x + y * y + z * z + w * w);
		return new Vec4(x/len, y/len, z/len, w/len);
	}
	
	public Vec3 toVec3() {
		return new Vec3(x, y, z);
	}
	
	public Vec4 clone() {
		return new Vec4(x, y, z, w);
	}
	
	// M x V (stores result in V)
	public Vec4 mul(Matrix4 m) {
		float n0 = x * m.m00 + y * m.m01 + z * m.m02 + w * m.m03;
		float n1 = x * m.m10 + y * m.m11 + z * m.m12 + w * m.m13;
		float n2 = x * m.m20 + y * m.m21 + z * m.m22 + w * m.m23;
		float n3 = x * m.m30 + y * m.m31 + z * m.m32 + w * m.m33;
		
		return new Vec4(n0, n1, n2, n3);
	}
}
