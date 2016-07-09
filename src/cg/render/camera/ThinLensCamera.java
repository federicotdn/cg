package cg.render.camera;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.rand.MultiJitteredSampler;
import cg.render.Image;
import cg.render.Ray;

/**
 * Created by Hobbit on 7/8/16.
 */
public class ThinLensCamera extends Camera {
    private double fovDegrees;
    private MultiJitteredSampler.SubSampler sampler;

    public ThinLensCamera(Vec3 t, Vec3 r, double fov) {
        super(t, r);
        this.fovDegrees = fov;
    }

    @Override
    protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
        double aspectRatio = img.aspectRatio();
        double halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));

        double ndcx = (pixelX + 0.5) / img.getWidth();
        double ndcy = (pixelY + 0.5) / img.getHeight();

        double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
        double py = (1 - (2 * ndcy)) * halfImagePlane;

        Vec2 diskSample = MathUtils.squareToDisk(offsetX, offsetY);
        diskSample = diskSample.mul(0.5);
        Vec3 origin3 = new Vec3(diskSample.x, diskSample.y, 0);

        Vec3 direction3 = new Vec3(px, py, 1).normalize();
        double ft = 8.0/ direction3.z;
        Vec3 pfocus = direction3.mul(ft);
        direction3 = pfocus.sub(origin3).normalize();

        Vec4 direction = direction3.asDirection();
        direction = transform.mulVec(direction);
        direction3 = direction.asVec3();


        Vec4 origin = origin3.asPosition();
        origin = transform.mulVec(origin);
        origin3 = origin.asVec3();

        return new Ray(origin3, direction3, null);
    }
}
