package cg.render.assets;

import cg.accelerator.MeshKDTree;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.QuickCollision;
import cg.render.Ray;
import cg.render.shapes.MeshInstance;

import java.util.List;

public class Mesh {
	private double[] v;
	private double[] n;
	private double[] uv;
	private int[] faces;
	private MeshKDTree kdTree;

	/*
	 * The Mesh class contains the actual geometry data used in MeshInstance.
	 * Mesh is NOT a Primitive.
	 */

	public Mesh(List<Double> v, List<Double> n, List<Double> uv, List<Integer> faces) {
		this.v = toDoubleArray(v);
		this.n = toDoubleArray(n);
		this.uv = toDoubleArray(uv);
		this.faces = toIntArray(faces);
		this.kdTree = new MeshKDTree(this, triangleCount());
	}

	public QuickCollision calculateCollision(Ray ray, MeshInstance mesh) {
		return kdTree.hit(ray, mesh);
//		QuickCollision closestCol = null;
//		for (int i =0; i< faces.length/9 ; i++) {
//			QuickCollision col = checkCollision(ray, i, mesh);
//
//			if (col == null) {
//				continue;
//			}
//
//			if (closestCol == null || col.getLocalT() < closestCol.getLocalT()) {
//				closestCol = col;
//			}
//		}
//
//		return closestCol;
	}

	public QuickCollision checkCollision(Ray ray, int index, MeshInstance mesh) {
		int faceIndex = index * 9;
		Vec3 p1 = vertexForIndex(faceIndex);
		Vec3 p2 = vertexForIndex(faceIndex + 3);
		Vec3 p3 = vertexForIndex(faceIndex + 6);

		Vec3 e1 = p2.sub(p1);
		Vec3 e2 = p3.sub(p1);

		Vec3 s1 = ray.getDirection().cross(e2); // pvec
		double divisor = s1.dot(e1); // det
		if (divisor < 0.00001) {
			return null;
		}
		double invDivisor = 1.0f/divisor;

		Vec3 d = ray.getOrigin().sub(p1); //tvec
		double b1 = d.dot(s1) * invDivisor; // u
		if (b1 < 0 || b1 > 1) {
			return null;
		}

		Vec3 s2 = d.cross(e1); // qvec
		double b2 = ray.getDirection().dot(s2) * invDivisor;
		if (b2 < 0 || b1 + b2 > 1) {
			return null;
		}

		double t = e2.dot(s2) * invDivisor;
		if (t < 0 || t > ray.getMaxT()) {
			return null;
		}

		QuickCollision qc = new QuickCollision(mesh, ray, null, t, -1);
		qc.setMeshData(faceIndex, b1, b2);
		return qc;
	}
	
	public Collision completeCollision(MeshInstance instance, QuickCollision qc) {
		int faceIndex = qc.getFaceIndex();
		double b1 = qc.getB1();
		double b2 = qc.getB2();
		
		Vec3 n1 = normalForIndex(faceIndex);
		Vec3 n2 = normalForIndex(faceIndex + 3);
		Vec3 n3 = normalForIndex(faceIndex + 6);

		Vec3 normal = interpolate(b1, b2, n1, n2, n3);

		double u = 0, v = 0;

		if (uv != null) {
			double u1 = uv[faces[faceIndex + 1] * 2];
			double u2 = uv[faces[faceIndex + 4] * 2];
			double u3 = uv[faces[faceIndex + 7] * 2];
			u = ((1 - b2 - b1)*u1) + (u2 * b1) + (u3 * b2);

			double v1 = uv[(faces[faceIndex + 1] * 2) + 1];
			double v2 = uv[(faces[faceIndex + 4] * 2) + 1];
			double v3 = uv[(faces[faceIndex + 7] * 2) + 1];
			v = ((1 - b2 - b1)*v1) + (v2 * b1) + (v3 * b2);
		}
		
		return new Collision(instance, qc.getLocalRay(), qc.getLocalT(), normal, u, v);
	}

	private Vec3 interpolate(double b1, double b2, Vec3  v1, Vec3 v2, Vec3 v3) {
		return v1.mul(1 - b2 - b1).sum(v2.mul(b1)).sum(v3.mul(b2)).normalize();
	}

	private Vec3 normalForIndex(int faceIndex) {
		int index = faces[faceIndex + 2];
		return new Vec3( n[(index * 3)],  n[(index * 3) + 1],  n[(index * 3) + 2]);
	}

	private Vec3 vertexForIndex(int faceIndex) {
		int index = faces[faceIndex];
		return new Vec3( v[(index * 3)],  v[(index * 3) + 1],  v[(index * 3) + 2]);
	}

	public double getAvg(int index, int axis) {
		int v1Index = faces[index * 9];
		int v2Index = faces[(index * 9) + 3];
		int v3Index = faces[(index * 9) + 6];

		double p1 = v[(v1Index * 3) + axis];
		double p2 = v[(v2Index * 3) + axis];
		double p3 = v[(v3Index * 3) + axis];

		return (p1 + p2 + p3)/3;
	}

	public void calculateMinAndMax(int index, int axis, double[] ans) {
		int v1Index = faces[index * 9];
		int v2Index = faces[(index * 9) + 3];
		int v3Index = faces[(index * 9) + 6];

		double p1 = v[(v1Index * 3) + axis];
		double p2 = v[(v2Index * 3) + axis];
		double p3 = v[(v3Index * 3) + axis];

		ans[0] = Math.min(p1, Math.min(p2, p3));
		ans[1] = Math.max(p1, Math.max(p2, p3));
	}

	private int triangleCount() {
		return faces.length/9;
	}

	public BoundingBox getBBox() {
		double pMinX = Double.MAX_VALUE;
		double pMinY = Double.MAX_VALUE;
		double pMinZ = Double.MAX_VALUE;

		double pMaxX = -Double.MAX_VALUE;
		double pMaxY = -Double.MAX_VALUE;
		double pMaxZ = -Double.MAX_VALUE;

		double x, y, z;
		for (int i =0; i < v.length; i+=3) {
			x = v[i];
			y = v[i + 1];
			z = v[i + 2];

			if (x < pMinX) {
				pMinX = x;
			} else if (x > pMaxX) {
				pMaxX = x;
			}

			if (y < pMinY) {
				pMinY = y;
			} else if (y > pMaxY) {
				pMaxY = y;
			}

			if (z < pMinZ) {
				pMinZ = z;
			} else if (z > pMaxZ) {
				pMaxZ = z;
			}

		}

		return new BoundingBox(new Vec3(pMinX, pMinY, pMinZ), new Vec3(pMaxX, pMaxY, pMaxZ));
	}

	private double[] toDoubleArray(List<Double> f) {
		if (f.size() == 0) {
			return null;
		}

		double[] a = new double[f.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = f.get(i);
		}

		return a;
	}

	private int[] toIntArray(List<Integer> f) {
		int[] a = new int[f.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = f.get(i);
		}

		return a;
	}
}
