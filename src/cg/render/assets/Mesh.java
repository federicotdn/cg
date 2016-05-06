package cg.render.assets;

import cg.accelerator.MeshKDTree;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Ray;
import cg.render.shapes.MeshInstance;

import java.util.List;

public class Mesh {
	private float[] v;
	private float[] n;
	private float[] uv;
	private int[] faces;
	private MeshKDTree kdTree;

	/*
	 * The Mesh class contains the actual geometry data used in MeshInstance.
	 * Mesh is NOT a Primitive.
	 */

	public Mesh(List<Float> v, List<Float> n, List<Float> uv, List<Integer> faces) {
		this.v = toFloatArray(v);
		this.n = toFloatArray(n);
		this.uv = toFloatArray(uv);
		this.faces = toIntArray(faces);
		this.kdTree = new MeshKDTree(this, triangleCount());
	}

	public Collision calculateCollision(Ray ray, MeshInstance mesh) {
		return kdTree.hit(ray, mesh);
	}

	public Collision checkCollision(Ray ray, int index, MeshInstance mesh) {
		int faceIndex = index * 9;
		Vec3 p1 = vertexForIndex(faceIndex);
		Vec3 p2 = vertexForIndex(faceIndex + 3);
		Vec3 p3 = vertexForIndex(faceIndex + 6);

		Vec3 e1 = p2.sub(p1);
		Vec3 e2 = p3.sub(p1);

		Vec3 s1 = ray.getDirection().cross(e2); // pvec
		float divisor = s1.dot(e1); // det
		if (divisor == 0) {
			return null;
		}
		float invDivisor = 1.0f/divisor;

		Vec3 d = ray.getOrigin().sub(p1); //tvec
		float b1 = d.dot(s1) * invDivisor; // u
		if (b1 < 0 || b1 > 1) {
			return null;
		}

		Vec3 s2 = d.cross(e1); // qvec
		float b2 = ray.getDirection().dot(s2) * invDivisor;
		if (b2 < 0 || b1 + b2 > 1) {
			return null;
		}

		float t = e2.dot(s2) * invDivisor;
		if (t < 0 || t > ray.getMaxT()) {
			return null;
		}

		Vec3 n1 = normalForIndex(faceIndex);
		Vec3 n2 = normalForIndex(faceIndex + 3);
		Vec3 n3 = normalForIndex(faceIndex + 6);

		Vec3 normal = interpolate(b1, b2, n1, n2, n3);
		float u1 = uv[faces[faceIndex + 1] * 2];
		float u2 = uv[faces[faceIndex + 4] * 2];
		float u3 = uv[faces[faceIndex + 7] * 2];
		float u = ((1 - b2 - b1)*u1) + (u2 * b1) + (u3 * b2);

		float v1 = uv[(faces[faceIndex + 1] * 2) + 1];
		float v2 = uv[(faces[faceIndex + 4] * 2) + 1];
		float v3 = uv[(faces[faceIndex + 7] * 2) + 1];
		float v = ((1 - b2 - b1)*v1) + (v2 * b1) + (v3 * b2);

		return new Collision(mesh, ray, t, normal, u, v);
	}

	private Vec3 interpolate(float b1, float b2, Vec3  v1, Vec3 v2, Vec3 v3) {
		return v1.mul(1 - b2 - b1).sum(v2.mul(b1)).sum(v3.mul(b2));
	}

	private Vec3 normalForIndex(int faceIndex) {
		int index = faces[faceIndex + 2];
		return new Vec3( n[(index * 3)],  n[(index * 3) + 1],  n[(index * 3) + 2]);
	}

	private Vec3 vertexForIndex(int faceIndex) {
		int index = faces[faceIndex];
		return new Vec3( v[(index * 3)],  v[(index * 3) + 1],  v[(index * 3) + 2]);
	}

	public float getAvg(int index, int axis) {
		int v1Index = faces[index * 9];
		int v2Index = faces[(index * 9) + 3];
		int v3Index = faces[(index * 9) + 6];

		float p1 = v[(v1Index * 3) + axis];
		float p2 = v[(v2Index * 3) + axis];
		float p3 = v[(v3Index * 3) + axis];

		return (p1 + p2 + p3)/3;
	}

	public void calculateMinAndMax(int index, int axis, float[] ans) {
		int v1Index = faces[index * 9];
		int v2Index = faces[(index * 9) + 3];
		int v3Index = faces[(index * 9) + 6];

		float p1 = v[(v1Index * 3) + axis];
		float p2 = v[(v2Index * 3) + axis];
		float p3 = v[(v3Index * 3) + axis];

		ans[0] = Math.min(p1, Math.min(p2, p3));
		ans[1] = Math.max(p1, Math.max(p2, p3));
	}

	private int triangleCount() {
		return faces.length/9;
	}

	public BoundingBox getBBox() {
		float pMinX = Float.MAX_VALUE;
		float pMinY = Float.MAX_VALUE;
		float pMinZ = Float.MAX_VALUE;

		float pMaxX = Float.MIN_VALUE;
		float pMaxY = Float.MIN_VALUE;
		float pMaxZ = Float.MIN_VALUE;

		float x, y, z;
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

	private float[] toFloatArray(List<Float> f) {
		float[] a = new float[f.size()];
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
