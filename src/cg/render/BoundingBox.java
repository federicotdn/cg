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

    public BoundingBox transformBBox(Matrix4 trs) {
        Vec3[] v = new Vec3[8];
        v[0] = pMin;
        v[1] = pMax;
        v[2] = new Vec3(v[1].x, v[0].y, v[0].z);
        v[3] = new Vec3(v[0].x, v[1].y, v[0].z);
        v[4] = new Vec3(v[1].x, v[0].y, v[1].z);
        v[5] = new Vec3(v[0].x, v[0].y, v[1].z);
        v[6] = new Vec3(v[0].x, v[1].y, v[1].z);
        v[7] = new Vec3(v[1].x, v[1].y, v[0].z);

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < v.length; i++) {
            Vec3 transformedPoint = trs.mulVec(v[i].asPosition()).asVec3();
            if (transformedPoint.x < minX) {
                minX = transformedPoint.x;
            }

            if (transformedPoint.x > maxX) {
                maxX = transformedPoint.x;
            }

            if (transformedPoint.y < minY) {
                minY = transformedPoint.y;
            }

            if (transformedPoint.y > maxY) {
                maxY = transformedPoint.y;
            }

            if (transformedPoint.z < minZ) {
                minZ = transformedPoint.z;
            }

            if (transformedPoint.z > maxZ) {
                maxZ = transformedPoint.z;
            }
        }

        return new BoundingBox(new Vec3(minX, minY, minZ), new Vec3(maxX, maxY, maxZ));

    }

    public Float collide(Ray ray) {
        return Box.collisionForBox(pMin, pMax, ray);
    }
}
