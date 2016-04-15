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

	public final float m00;
	public final float m01;
	public final float m02;
	public final float m03;
	public final float m10;
	public final float m11;
	public final float m12;
	public final float m13;
	public final float m20;
	public final float m21;
	public final float m22;
	public final float m23;
	public final float m30;
	public final float m31;
	public final float m32;
	public final float m33;

	public Matrix4() {
		this(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		);
	}

	public Matrix4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
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

	public Matrix4(float[] vals) {
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

	public static Matrix4 rotationX(float deg) {
		float rad = (float) Math.toRadians(deg);

		float cosAngle = (float) Math.cos(rad);
		float sinAngle = (float) Math.sin(rad);

		return new Matrix4(
				1, 0, 0, 0,
				0, cosAngle, -sinAngle, 0,
				0, sinAngle, cosAngle, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4 rotationY(float deg) {
		float rad = (float) Math.toRadians(deg);

		float cosAngle = (float) Math.cos(rad);
		float sinAngle = (float) Math.sin(rad);

		return new Matrix4(
				cosAngle, 0, sinAngle, 0,
				0, 1, 0, 0,
				-sinAngle, 0, cosAngle, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4 rotationZ(float deg) {
		float rad = (float) Math.toRadians(deg);

		float cosAngle = (float) Math.cos(rad);
		float sinAngle = (float) Math.sin(rad);

		return new Matrix4(
				cosAngle, -sinAngle, 0, 0,
				sinAngle, cosAngle, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		);
	}

	public Matrix4 inverse() {
		/* Generated with autojava.py */
		float c00 = m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m31 * m22 * m13 - m32 * m23 * m11 - m21 * m12 * m33;
		float c01 = m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m30 * m22 * m13 - m32 * m23 * m10 - m20 * m12 * m33;
		float c02 = m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m30 * m21 * m13 - m31 * m23 * m10 - m20 * m11 * m33;
		float c03 = m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m30 * m21 * m12 - m31 * m22 * m10 - m20 * m11 * m32;
		float c10 = m01 * m22 * m33 + m02 * m23 * m31 + m03 * m21 * m32 - m31 * m22 * m03 - m32 * m23 * m01 - m21 * m02 * m33;
		float c11 = m00 * m22 * m33 + m02 * m23 * m30 + m03 * m20 * m32 - m30 * m22 * m03 - m32 * m23 * m00 - m20 * m02 * m33;
		float c12 = m00 * m21 * m33 + m01 * m23 * m30 + m03 * m20 * m31 - m30 * m21 * m03 - m31 * m23 * m00 - m20 * m01 * m33;
		float c13 = m00 * m21 * m32 + m01 * m22 * m30 + m02 * m20 * m31 - m30 * m21 * m02 - m31 * m22 * m00 - m20 * m01 * m32;
		float c20 = m01 * m12 * m33 + m02 * m13 * m31 + m03 * m11 * m32 - m31 * m12 * m03 - m32 * m13 * m01 - m11 * m02 * m33;
		float c21 = m00 * m12 * m33 + m02 * m13 * m30 + m03 * m10 * m32 - m30 * m12 * m03 - m32 * m13 * m00 - m10 * m02 * m33;
		float c22 = m00 * m11 * m33 + m01 * m13 * m30 + m03 * m10 * m31 - m30 * m11 * m03 - m31 * m13 * m00 - m10 * m01 * m33;
		float c23 = m00 * m11 * m32 + m01 * m12 * m30 + m02 * m10 * m31 - m30 * m11 * m02 - m31 * m12 * m00 - m10 * m01 * m32;
		float c30 = m01 * m12 * m23 + m02 * m13 * m21 + m03 * m11 * m22 - m21 * m12 * m03 - m22 * m13 * m01 - m11 * m02 * m23;
		float c31 = m00 * m12 * m23 + m02 * m13 * m20 + m03 * m10 * m22 - m20 * m12 * m03 - m22 * m13 * m00 - m10 * m02 * m23;
		float c32 = m00 * m11 * m23 + m01 * m13 * m20 + m03 * m10 * m21 - m20 * m11 * m03 - m21 * m13 * m00 - m10 * m01 * m23;
		float c33 = m00 * m11 * m22 + m01 * m12 * m20 + m02 * m10 * m21 - m20 * m11 * m02 - m21 * m12 * m00 - m10 * m01 * m22;
		/* Generated with autojava.py - end */

		float det = m00 * c00 - m01 * c01 + m02 * c02 - m03 * c03;
		if (det == 0.0f) {
			throw new RuntimeException("Matrix does not have inverse");
		}
		float idet = 1.0f / det;

		float n00 = c00 * idet;
		float n01 = -c10 * idet;
		float n02 = c20 * idet;
		float n03 = -c30 * idet;

		float n10 = -c01 * idet;
		float n11 = c11 * idet;
		float n12 = -c21 * idet;
		float n13 = c31 * idet;

		float n20 = c02 * idet;
		float n21 = -c12 * idet;
		float n22 = c22 * idet;
		float n23 = -c32 * idet;

		float n30 = -c03 * idet;
		float n31 = c13 * idet;
		float n32 = -c23 * idet;
		float n33 = c33 * idet;

		return new Matrix4(
				n00, n01, n02, n03,
				n10, n11, n12, n13,
				n20, n21, n22, n23,
				n30, n31, n32, n33);
	}

	public Matrix4 mul(Matrix4 m) {
		float t00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
		float t01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
		float t02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
		float t03 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;

		float t10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
		float t11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
		float t12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
		float t13 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;

		float t20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
		float t21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
		float t22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
		float t23 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;

		float t30 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
		float t31 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
		float t32 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
		float t33 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;

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
		float n0 = v.x * m00 + v.y * m01 + v.z * m02 + v.w * m03;
		float n1 = v.x * m10 + v.y * m11 + v.z * m12 + v.w * m13;
		float n2 = v.x * m20 + v.y * m21 + v.z * m22 + v.w * m23;
		float n3 = v.x * m30 + v.y * m31 + v.z * m32 + v.w * m33;

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
