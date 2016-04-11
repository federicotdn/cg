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
}
