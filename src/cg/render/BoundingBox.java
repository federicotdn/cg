package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

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

    public BoundingBox calculateBBox(Matrix4 trs) {
        return null;

    }
}
