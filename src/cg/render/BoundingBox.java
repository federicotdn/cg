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

    public Vec3 getCenter() {
        Vec3 dir = pMax.sub(pMin);
        return pMin.sum(dir.mul(0.5f));
    }

    public BoundingBox calculateBBox(Matrix4 trs) {
        return new BoundingBox(trs.mulVec(pMin.asPosition()).asVec3(), trs.mulVec(pMax.asPosition()).asVec3());

    }

    public Float collide(Ray ray) {
        float t0 = 0f, t1 = ray.getMaxT();
        float invRayDir = 1.f / ray.getDirection().x;
        float tNear = (pMin.x  - ray.getOrigin().x) * invRayDir;
        float tFar =  (pMax.x  - ray.getOrigin().x) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        invRayDir = 1.f / ray.getDirection().y;
        tNear = (pMin.y  - ray.getOrigin().y) * invRayDir;
        tFar =  (pMax.y  - ray.getOrigin().y) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        invRayDir = 1.f / ray.getDirection().z;
        tNear = (pMin.z  - ray.getOrigin().z) * invRayDir;
        tFar =  (pMax.z  - ray.getOrigin().z) * invRayDir;

        if (tNear > tFar) {
            float aux = tNear;
            tNear = tFar;
            tFar = aux;
        }

        t0 = tNear > t0 ? tNear : t0;
        t1 = tFar  < t1 ? tFar  : t1;
        if (t0 > t1) return null;

        return t0;
    }


}
