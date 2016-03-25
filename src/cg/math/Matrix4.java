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
}
