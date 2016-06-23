package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
import cg.render.Ray;

public class FinitePlane extends Primitive {
	private static final double EPSILON = 0.000001f;
		
	private final double halfWidth;
	private final double halfDepth;
	private final Vec3 normal;
	
	public FinitePlane(double width, double depth, Vec3 t, Vec3 r, Vec3 s) {
		this(width, depth, t, r, s, InfinitePlane.PLANE_NORMAL);
	}

	public FinitePlane(double width, double depth, Vec3 t, Vec3 r, Vec3 s, Vec3 normal) {
		this.halfWidth = width / 2;
		this.halfDepth = depth / 2;
		this.normal = normal;
		setTransform(t, r, s);
	}

	@Override
	public BoundingBox calculateBBox(Matrix4 trs) {
		Vec3 pMin, pMax;
		if (normal.x == 1) {
			pMax = new Vec3(EPSILON, halfDepth, halfWidth);
			pMin = new Vec3(-EPSILON, -halfDepth, -halfWidth);
		} else if (normal.z == 1) {
			pMax = new Vec3(halfWidth, halfDepth, EPSILON);
			pMin = new Vec3(-halfWidth, -halfDepth, -EPSILON);
		} else {
			pMax = new Vec3(halfWidth, EPSILON, halfDepth);
			pMin = new Vec3(-halfWidth, -EPSILON, -halfDepth);
		}

		return (new BoundingBox(pMin, pMax)).transformBBox(trs);
	}

	@Override
	public QuickCollision internalQuickCollideWith(Ray ray) {
        double t = InfinitePlane.planeT(ray, normal, 0);
        if (t < 0 || t > ray.getMaxT()) {
        	return null;
        }
        
        Vec3 colPos = ray.runDistance(t);
        if ((Math.abs(colPos.x) > halfWidth) || (Math.abs(colPos.z) > halfDepth)) {
        	return null;
        }
        
        return new QuickCollision(this, ray, null, t, -1);
	}

	@Override
	public Collision internalCompleteCollision(QuickCollision qc) {
		Vec3 colPos = qc.getLocalPosition();
		double u = Math.abs(colPos.x - halfWidth) / (halfWidth*2);
		double v = Math.abs(colPos.z - halfDepth) / (halfDepth * 2);
		return new Collision(this, qc.getLocalRay(), qc.getLocalT(), normal, u, v);
	}

}
