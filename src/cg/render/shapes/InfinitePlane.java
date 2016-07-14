package cg.render.shapes;

import cg.math.MathUtils;
import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
import cg.render.Ray;

/**
 * Created by Hobbit on 4/14/16.
 */
public class InfinitePlane extends Primitive {
    public static final Vec3 PLANE_NORMAL = new Vec3(0, 1, 0);

    public InfinitePlane(Vec3 t, Vec3 r, Vec3 s) {
    	setTransform(t, r, s);
    }

    public static double planeT(Ray ray, Vec3 normal, double d) {
        Vec3 rDir = ray.getDirection();
        Vec3 rPos = ray.getOrigin();
        double collide = rDir.dot(normal);

        if (collide < 0) {
            double t = -((rPos.dot(normal) + d)/rDir.dot(normal));
            if (t > 0 && t <= ray.getMaxT()) {
                return t;
            }
        }

        return -1;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return null;
    }

	@Override
	protected QuickCollision internalQuickCollideWith(Ray ray) {
        double t = planeT(ray, PLANE_NORMAL, 0);
        if (t < 0 || t > ray.getMaxT()) {
        	return null;
        }
        return new QuickCollision(this, ray, null, t, -1);
	}

	@Override
	protected Collision internalCompleteCollision(QuickCollision qc) {
		Vec3 colPos = qc.getLocalPosition();

        double u =  colPos.x / 10;
        double v = colPos.z / 10;

        Vec3 normal = PLANE_NORMAL;

        if (getMaterial().hasNormalMap()) {
            Vec3 mapNormal = getMaterial().getNormal(u, v);
            Vec3 tan = new Vec3(1,0,0);
            Vec3 bitan = tan.cross(PLANE_NORMAL).normalize();
            normal = MathUtils.tbn(tan, bitan, normal, mapNormal);
        }

		return new Collision(this, qc.getLocalRay(), qc.getLocalT(), normal, u, v);
	}
}
