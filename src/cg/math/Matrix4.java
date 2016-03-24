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
	
	private float m00;
	private float m01;
	private float m02;
	private float m03;
	private float m10;
	private float m11;
	private float m12;
	private float m13;
	private float m20;
	private float m21;
	private float m22;
	private float m23;
	private float m30;
	private float m31;
	private float m32;
	private float m33;
	
	public Matrix4() {
		m00 = m11 = m22 = m33 = 1;
	}
}
