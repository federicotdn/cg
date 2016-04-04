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
	
	public float m00;
	public float m01;
	public float m02;
	public float m03;
	public float m10;
	public float m11;
	public float m12;
	public float m13;
	public float m20;
	public float m21;
	public float m22;
	public float m23;
	public float m30;
	public float m31;
	public float m32;
	public float m33;
	
	public Matrix4() {
		m00 = m11 = m22 = m33 = 1;
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
		Matrix4 m = new Matrix4();
		
		m.m03 = t.x;
		m.m13 = t.y;
		m.m23 = t.z;
		
		return m;
	}
	
	public static Matrix4 scaleFromVec(Vec3 s) {
		Matrix4 m = new Matrix4();
		
		m.m00 = s.x;
		m.m11 = s.y;
		m.m22 = s.z;
		
		return m;		
	}
	
	public static Matrix4 rotationX(float deg) {
		float rad = (float)Math.toRadians(deg);
		Matrix4 m = new Matrix4();
		
		float cosAngle = (float)Math.cos(rad);
		float sinAngle = (float)Math.sin(rad);
		
		m.m11 = cosAngle;
		m.m22 = cosAngle;
		m.m12 = -sinAngle;
		m.m21 = sinAngle;
		
		return m;
	}
	
	public static Matrix4 rotationY(float deg) {
		float rad = (float)Math.toRadians(deg);
		Matrix4 m = new Matrix4();
		
		float cosAngle = (float)Math.cos(rad);
		float sinAngle = (float)Math.sin(rad);
		
		m.m00 = cosAngle;
		m.m02 = sinAngle;
		m.m20 = -sinAngle;
		m.m22 = cosAngle;
		
		return m;
	}
	
	public static Matrix4 rotationZ(float deg) {
		float rad = (float)Math.toRadians(deg);
		Matrix4 m = new Matrix4();
		
		float cosAngle = (float)Math.cos(rad);
		float sinAngle = (float)Math.sin(rad);
		
		m.m00 = cosAngle;
		m.m01 = -sinAngle;
		m.m11 = cosAngle;
		m.m10 = sinAngle;
		
		return m;
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
			return null;
		}
		float idet = 1.0f / det;
		
		Matrix4 i = new Matrix4();
		i.m00 = c00 * idet;
		i.m01 = -c10 * idet;
		i.m02 = c20 * idet;
		i.m03 = -c30 * idet;
		
		i.m10 = -c01 * idet;
		i.m11 = c11 * idet;
		i.m12 = -c21 * idet;
		i.m13 = c31 * idet;
		
		i.m20 = c02 * idet;
		i.m21 = -c12 * idet;
		i.m22 = c22 * idet;
		i.m23 = -c32 * idet;
		
		i.m30 = -c03 * idet;
		i.m31 = c13 * idet;
		i.m32 = -c23 * idet;
		i.m33 = c33 * idet;
		
		return i;
	}
	 
	public Matrix4 mult(Matrix4 m) {
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
		
		m00 = t00;
		m01 = t01;
		m02 = t02;
		m03 = t03;
		
		m10 = t10;
		m11 = t11;
		m12 = t12;
		m13 = t13;
		
		m20 = t20;
		m21 = t21;
		m22 = t22;
		m23 = t23;
		
		m30 = t30;
		m31 = t31;
		m32 = t32;
		m33 = t33;
		
		return this;
	}
	
	public Matrix4 clone() {
		Matrix4 m = new Matrix4();
		m.m00 = m00;
		m.m01 = m01;
		m.m02 = m02;
		m.m03 = m03;
		m.m10 = m10;
		m.m11 = m11;
		m.m12 = m12;
		m.m13 = m13;
		m.m20 = m20;
		m.m21 = m21;
		m.m22 = m22;
		m.m23 = m23;
		m.m30 = m30;
		m.m31 = m31;
		m.m32 = m32;
		m.m33 = m33;
		return m;
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
