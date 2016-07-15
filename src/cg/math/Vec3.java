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

	public final double x;
	public final double y;
	public final double z;

	public static final Vec3 NEG_X_AXIS = new Vec3(-1, 0, 0);
	public static final Vec3 NEG_Y_AXIS = new Vec3(0, -1, 0);
	public static final Vec3 NEG_Z_AXIS = new Vec3(0, 0, -1);
	public static final Vec3 X_AXIS = new Vec3(1, 0, 0);
	public static final Vec3 Y_AXIS = new Vec3(0, 1, 0);
	public static final Vec3 Z_AXIS = new Vec3(0, 0, 1);

	public Vec3() {
		this(0,0,0);
	}

	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 mul(double scalar) {
		return new Vec3(x * scalar, y * scalar, z * scalar);
	}

	public Vec3 div(double scalar) {
		return mul(1/scalar);
	}

	public Vec3 sum(Vec3 v) {
		return new Vec3(x + v.x, y + v.y, z + v.z);
	}

	public Vec3 sub(Vec3 v) {
		return new Vec3(x - v.x, y - v.y, z - v.z);
	}

	public double dot(Vec3 v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public Vec3 cross(Vec3 v) {
		return new Vec3((y * v.z) - (z * v.y),(z * v.x) - (x * v.z), (x * v.y) - (y * v.x));
	}

	public Vec4 asDirection() {
		return new Vec4(x, y, z, 0);
	}

	public double getCoordByAxis(int axis) {
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

	public Vec3 pow(double pow) {
		return new Vec3((double) Math.pow(x, pow), (double) Math.pow(y, pow), (double) Math.pow(z, pow));
	}

	public Vec3 max(double value) {
		return new Vec3((double) Math.max(x, value), (double) Math.max(y, value), (double) Math.max(z, value));

	}

	public Vec3 getSmallestAxis() {
		double absX = Math.abs(x);
		double absY = Math.abs(y);
		double absZ = Math.abs(z);

		if (absX <= absY && absX <= absZ) {
			return new Vec3(1,0,0);
		}

		if (absY <= absX && absY <= absZ) {
			return new Vec3(0,1,0);
		}

		return new Vec3(0,0,1);
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
		double len = len();
		return new Vec3(x/len, y/len, z/len);
	}

	public Vec3 reflect(Vec3 normal) {
		return normal.mul(2 * normal.dot(this)).sub(this);
	}

	public double len() {
		return (double) Math.sqrt(x * x + y * y + z * z);
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
