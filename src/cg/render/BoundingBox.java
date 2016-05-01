package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.shapes.Box;

/**
 * Created by Hobbit on 4/10/16.
 */
public class BoundingBox {
    public final Vec3 pMin;
    public final Vec3 pMax;

    public BoundingBox(Vec3 pMin, Vec3 pMax) {
        this.pMin = pMin;
        this.pMax = pMax;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "pMin=" + pMin +
                ", pMax=" + pMax +
                '}';
    }

    public Vec3 getCenter() {
        Vec3 dir = pMax.sub(pMin);
        return pMin.sum(dir.mul(0.5f));
    }

    public BoundingBox calculateBBox(Matrix4 trs) {
    	//TODO: Fix this
        return new BoundingBox(trs.mulVec(pMin.asPosition()).asVec3(), trs.mulVec(pMax.asPosition()).asVec3());

    }

    public Float collide(Ray ray) {
        return Box.collisionForBox(pMin, pMax, ray);
    }
}
