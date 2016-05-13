package cg.math;

public class Matrix4 {
	/*
	 * 4x4 Matrix
	 * 
	 * ---                ---
	 * | m00  m01  m02  m03 |
	 * |                    |
	 * | m10  m11  m12  m13 |
	 * |                    |
	 * | m20  m21  m22  m23 |
	 * |                    |
	 * | m30  m31  m32  m33 |
	 * ---                ---
	 */

	public final double m00;
	public final double m01;
	public final double m02;
	public final double m03;
	public final double m10;
	public final double m11;
	public final double m12;
	public final double m13;
	public final double m20;
	public final double m21;
	public final double m22;
	public final double m23;
	public final double m30;
	public final double m31;
	public final double m32;
	public final double m33;

	public Matrix4() {
		this(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		);
	}

	public Matrix4(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public Matrix4(double[] vals) {
		if (vals.length != 16) {
			throw new RuntimeException("Matrix4 array initializer must contain 16 values.");
		}

		m00 = vals[0];
		m01 = vals[1];
		m02 = vals[2];
		m03 = vals[3];

		m10 = vals[4];
		m11 = vals[5];
		m12 = vals[6];
		m13 = vals[7];

		m20 = vals[8];
		m21 = vals[9];
		m22 = vals[10];
		m23 = vals[11];

		m30 = vals[12];
		m31 = vals[13];
		m32 = vals[14];
		m33 = vals[15];
	}

	public static Matrix4 transFromVec(Vec3 t) {
		return new Matrix4(
				1, 0, 0, t.x,
				0, 1, 0, t.y,
				0, 0, 1, t.z,
				0, 0, 0, 1
		);
	}

	public static Matrix4 scaleFromVec(Vec3 s) {
		return new Matrix4(
				s.x, 0, 0, 0,
				0, s.y, 0, 0,
				0, 0, s.z, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4 rotationX(double deg) {
		double rad = (double) Math.toRadians(deg);

		double cosAngle = (double) Math.cos(rad);
		double sinAngle = (double) Math.sin(rad);

		return new Matrix4(
				1, 0, 0, 0,
				0, cosAngle, -sinAngle, 0,
				0, sinAngle, cosAngle, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4 rotationY(double deg) {
		double rad = (double) Math.toRadians(deg);

		double cosAngle = (double) Math.cos(rad);
		double sinAngle = (double) Math.sin(rad);

		return new Matrix4(
				cosAngle, 0, sinAngle, 0,
				0, 1, 0, 0,
				-sinAngle, 0, cosAngle, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4 rotationZ(double deg) {
		double rad = (double) Math.toRadians(deg);

		double cosAngle = (double) Math.cos(rad);
		double sinAngle = (double) Math.sin(rad);

		return new Matrix4(
				cosAngle, -sinAngle, 0, 0,
				sinAngle, cosAngle, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		);
	}

	public Matrix4 inverse() {
		/* Generated with autojava.py */
		double c00 = m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m31 * m22 * m13 - m32 * m23 * m11 - m21 * m12 * m33;
		double c01 = m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m30 * m22 * m13 - m32 * m23 * m10 - m20 * m12 * m33;
		double c02 = m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m30 * m21 * m13 - m31 * m23 * m10 - m20 * m11 * m33;
		double c03 = m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m30 * m21 * m12 - m31 * m22 * m10 - m20 * m11 * m32;
		double c10 = m01 * m22 * m33 + m02 * m23 * m31 + m03 * m21 * m32 - m31 * m22 * m03 - m32 * m23 * m01 - m21 * m02 * m33;
		double c11 = m00 * m22 * m33 + m02 * m23 * m30 + m03 * m20 * m32 - m30 * m22 * m03 - m32 * m23 * m00 - m20 * m02 * m33;
		double c12 = m00 * m21 * m33 + m01 * m23 * m30 + m03 * m20 * m31 - m30 * m21 * m03 - m31 * m23 * m00 - m20 * m01 * m33;
		double c13 = m00 * m21 * m32 + m01 * m22 * m30 + m02 * m20 * m31 - m30 * m21 * m02 - m31 * m22 * m00 - m20 * m01 * m32;
		double c20 = m01 * m12 * m33 + m02 * m13 * m31 + m03 * m11 * m32 - m31 * m12 * m03 - m32 * m13 * m01 - m11 * m02 * m33;
		double c21 = m00 * m12 * m33 + m02 * m13 * m30 + m03 * m10 * m32 - m30 * m12 * m03 - m32 * m13 * m00 - m10 * m02 * m33;
		double c22 = m00 * m11 * m33 + m01 * m13 * m30 + m03 * m10 * m31 - m30 * m11 * m03 - m31 * m13 * m00 - m10 * m01 * m33;
		double c23 = m00 * m11 * m32 + m01 * m12 * m30 + m02 * m10 * m31 - m30 * m11 * m02 - m31 * m12 * m00 - m10 * m01 * m32;
		double c30 = m01 * m12 * m23 + m02 * m13 * m21 + m03 * m11 * m22 - m21 * m12 * m03 - m22 * m13 * m01 - m11 * m02 * m23;
		double c31 = m00 * m12 * m23 + m02 * m13 * m20 + m03 * m10 * m22 - m20 * m12 * m03 - m22 * m13 * m00 - m10 * m02 * m23;
		double c32 = m00 * m11 * m23 + m01 * m13 * m20 + m03 * m10 * m21 - m20 * m11 * m03 - m21 * m13 * m00 - m10 * m01 * m23;
		double c33 = m00 * m11 * m22 + m01 * m12 * m20 + m02 * m10 * m21 - m20 * m11 * m02 - m21 * m12 * m00 - m10 * m01 * m22;
		/* Generated with autojava.py - end */

		double det = m00 * c00 - m01 * c01 + m02 * c02 - m03 * c03;
		if (det == 0.0f) {
			throw new RuntimeException("Matrix does not have inverse");
		}
		double idet = 1.0f / det;

		double n00 = c00 * idet;
		double n01 = -c10 * idet;
		double n02 = c20 * idet;
		double n03 = -c30 * idet;

		double n10 = -c01 * idet;
		double n11 = c11 * idet;
		double n12 = -c21 * idet;
		double n13 = c31 * idet;

		double n20 = c02 * idet;
		double n21 = -c12 * idet;
		double n22 = c22 * idet;
		double n23 = -c32 * idet;

		double n30 = -c03 * idet;
		double n31 = c13 * idet;
		double n32 = -c23 * idet;
		double n33 = c33 * idet;

		return new Matrix4(
				n00, n01, n02, n03,
				n10, n11, n12, n13,
				n20, n21, n22, n23,
				n30, n31, n32, n33);
	}

	public Matrix4 mul(Matrix4 m) {
		double t00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
		double t01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
		double t02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
		double t03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;

		double t10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
		double t11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
		double t12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
		double t13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;

		double t20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
		double t21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
		double t22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
		double t23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;

		double t30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
		double t31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
		double t32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
		double t33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;

		return new Matrix4(
				t00, t01, t02, t03,
				t10, t11, t12, t13,
				t20, t21, t22, t23,
				t30, t31, t32, t33);
	}

	public Matrix4 traspose() {
		return new Matrix4(
				m00, m10, m20, m30,
				m01, m11, m21, m31,
				m02, m12, m22, m32,
				m03, m13, m23, m33
		);
	}

	public Matrix4 clone() {
		return new Matrix4(
				m00, m01, m02, m03,
				m10, m11, m12, m13,
				m20, m21, m22, m23,
				m30, m31, m32, m33);
	}

	public Vec4 mulVec(Vec4 v) {
		double n0 = v.x * m00 + v.y * m01 + v.z * m02 + v.w * m03;
		double n1 = v.x * m10 + v.y * m11 + v.z * m12 + v.w * m13;
		double n2 = v.x * m20 + v.y * m21 + v.z * m22 + v.w * m23;
		double n3 = v.x * m30 + v.y * m31 + v.z * m32 + v.w * m33;

		return new Vec4(n0, n1, n2, n3);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String fmt = "%10.4f";

		sb.append("| ");
		sb.append(String.format(fmt, m00) + " ");
		sb.append(String.format(fmt, m01) + " ");
		sb.append(String.format(fmt, m02) + " ");
		sb.append(String.format(fmt, m03) + " |\n");

		sb.append("| ");
		sb.append(String.format(fmt, m10) + " ");
		sb.append(String.format(fmt, m11) + " ");
		sb.append(String.format(fmt, m12) + " ");
		sb.append(String.format(fmt, m13) + " |\n");

		sb.append("| ");
		sb.append(String.format(fmt, m20) + " ");
		sb.append(String.format(fmt, m21) + " ");
		sb.append(String.format(fmt, m22) + " ");
		sb.append(String.format(fmt, m23) + " |\n");

		sb.append("| ");
		sb.append(String.format(fmt, m30) + " ");
		sb.append(String.format(fmt, m31) + " ");
		sb.append(String.format(fmt, m32) + " ");
		sb.append(String.format(fmt, m33) + " |");

		return sb.toString();
	}
}
