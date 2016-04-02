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
		String fmt = "%5.2f";
		
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
