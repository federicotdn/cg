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
	
	public Vec3 sum(Vec3 v) {
		return new Vec3(x + v.x, y + v.y, z + v.z);
	}
	
	public Vec3 sub(Vec3 v) {
		return new Vec3(x - v.x, y - v.y, z - v.z);
	}
	
	public float dot(Vec3 v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public Vec3 cross(Vec3 v) {
		return new Vec3((y * v.z) - (z * v.y),(z * v.x) - (x * v.z), (x * v.y) - (y * v.x));
	}
	
	public Vec4 asDirection() {
		return new Vec4(x, y, z, 0);
	}

	public float getCoordByAxis(int axis) {
		switch (axis) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				throw new IllegalArgumentException("Invalid axis");
		}
	}

	public static Vec3 axisVec(int axis) {
		switch (axis) {
			case 0:
				return new Vec3(1,0,0);
			case 1:
				return new Vec3(0,1,0);
			case 2:
				return new Vec3(0,0,1);
			default:
				throw new IllegalArgumentException("Invalid axis");
		}
	}

	public Vec3 pow(float pow) {
		return new Vec3((float) Math.pow(x, pow), (float) Math.pow(y, pow), (float) Math.pow(z, pow));
	}

	public Vec3 max(float value) {
		return new Vec3((float) Math.max(x, value), (float) Math.max(y, value), (float) Math.max(z, value));

	}

	public Vec3 max(Vec3 v) {
		return new Vec3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
	}

	public Vec3 min(Vec3 v) {
		return new Vec3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
	}
	
	public Vec4 asPosition() {
		return new Vec4(x, y, z, 1);
	}
	
	public Vec3 normalize() {
		float len = len();
		return new Vec3(x/len, y/len, z/len);
	}

	public Vec3 reflect(Vec3 normal) {
		return normal.mul(2 * normal.dot(this)).sub(this);
	}
	
	public float len() {
		return (float) Math.sqrt(x * x + y * y + z * z);
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
