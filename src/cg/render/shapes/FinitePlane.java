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
	
	public FinitePlane(double width, double depth, Vec3 t, Vec3 r, Vec3 s) {
		this.halfWidth = width / 2;
		this.halfDepth = depth / 2;
		setTransform(t, r, s);
	}
	
	@Override
	protected Collision calculateCollision(Ray ray) {
        Double t = InfinitePlane.planeT(ray, InfinitePlane.PLANE_NORMAL, 0);
        if (t == null || t > ray.getMaxT()) {
        	return null;
        }
        
        Vec3 colPos = ray.runDistance(t);
        if ((Math.abs(colPos.x) > halfWidth) || (Math.abs(colPos.z) > halfDepth)) {
        	return null;
        }
        
        return new Collision(this, ray, t, InfinitePlane.PLANE_NORMAL, Math.abs(colPos.x - halfWidth)/(halfWidth*2), Math.abs(colPos.z - halfDepth)/(halfDepth * 2));
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		Vec3 pMax = new Vec3(halfWidth, EPSILON, halfDepth);
		Vec3 pMin = new Vec3(-halfWidth, -EPSILON, -halfDepth);
		return (new BoundingBox(pMin, pMax)).transformBBox(trs);
	}

	@Override
	protected QuickCollision internalQuickCollideWith(Ray ray) {
        Double t = InfinitePlane.planeT(ray, InfinitePlane.PLANE_NORMAL, 0);
        if (t == null || t > ray.getMaxT()) {
        	return null;
        }
        
        Vec3 colPos = ray.runDistance(t);
        if ((Math.abs(colPos.x) > halfWidth) || (Math.abs(colPos.z) > halfDepth)) {
        	return null;
        }
        
        return new QuickCollision(this, ray, null, t, -1);
	}

	@Override
	protected Collision internalCompleteCollision(QuickCollision qc) {
		Vec3 colPos = qc.getLocalPosition();
		double u = Math.abs(colPos.x - halfWidth) / (halfWidth*2);
		double v = Math.abs(colPos.z - halfDepth) / (halfDepth * 2);
		return new Collision(this, qc.getLocalRay(), qc.getLocalT(), InfinitePlane.PLANE_NORMAL, u, v);
	}

}
