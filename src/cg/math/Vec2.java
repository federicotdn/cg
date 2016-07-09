package cg.math;

public class Vec2 {
	public final double x;
	public final double y;
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 mul(double mul) {
		return new Vec2(x * mul, y * mul);
	}

	@Override
	public String toString() {
		return "Vec2 [x=" + x + ", y=" + y + "]";
	}
}
