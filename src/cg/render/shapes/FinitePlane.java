package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

public class FinitePlane extends Primitive {
	private static final float EPSILON = 0.000001f;
		
	private final float halfWidth;
	private final float halfDepth;
	
	public FinitePlane(float width, float depth, Vec3 t, Vec3 r, Vec3 s) {
		this.halfWidth = width / 2;
		this.halfDepth = depth / 2;
		setTransform(t, r, s);
	}
	
	@Override
	protected Collision calculateCollision(Ray ray) {
        Float t = InfinitePlane.planeT(ray, InfinitePlane.PLANE_NORMAL, 0);
        if (t == null || t > ray.getMaxT()) {
        	return null;
        }
        
        Vec3 colPos = ray.runDistance(t);
        if ((Math.abs(colPos.x) > halfWidth) || (Math.abs(colPos.z) > halfDepth)) {
        	return null;
        }
        
        return new Collision(this, ray, t, InfinitePlane.PLANE_NORMAL, 0.0f, 0.0f);
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		//TODO: Is EPSILON necessary?
		Vec3 pMax = new Vec3(halfWidth, EPSILON, halfDepth);
		Vec3 pMin = new Vec3(-halfWidth, -EPSILON, -halfDepth);
		return (new BoundingBox(pMin, pMax)).transformBBox(trs);
	}

}
