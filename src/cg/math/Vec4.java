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

	public final double x;
	public final double y;
	public final double z;
	public final double w;
	
	public Vec4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4 normalize() {
		double len = (double) Math.sqrt(x * x + y * y + z * z + w * w);
		return new Vec4(x/len, y/len, z/len, w/len);
	}
	
	public Vec3 asVec3() {
		return new Vec3(x, y, z);
	}
	
	public Vec4 clone() {
		return new Vec4(x, y, z, w);
	}
}
