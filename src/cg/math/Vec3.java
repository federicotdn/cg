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

	public final float x;
	public final float y;
	public final float z;
	
	public Vec3() {
		/* float fields initialized to 0.0f by default */
		this(0,0,0);
	}
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 mul(float scalar) {
		return new Vec3(x * scalar, y * scalar, z * scalar);
	}
	
	public Vec4 asDirection() {
		return new Vec4(x, y, z, 0);
	}
	
	public Vec4 asPosition() {
		return new Vec4(x, y, z, 1);
	}
	
	public Vec3 normalize() {
		float len = (float) Math.sqrt(x * x + y * y + z * z);
		return new Vec3(x/len, y/len, z/len);
	}

	@Override
	public String toString() {
		return "Vec3{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				'}';
	}
}
