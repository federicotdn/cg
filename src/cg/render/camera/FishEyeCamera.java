package cg.render.camera;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.Image;
import cg.render.Ray;

public class FishEyeCamera extends Camera {
    public FishEyeCamera(Vec3 t, Vec3 r) {
        super(t, r);
    }

    @Override
    protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
        double ndcx = (pixelX + offsetX) / img.getWidth();
        double ndcy = (pixelY + offsetY) / img.getHeight();

        double px = (2 * ndcx) - 1;
        double py = (2 * ndcy) - 1;
        double rSquared = px * px + py * py;
        Vec3 dir;

        double psi_max = Math.PI / 2;

        if (rSquared <= 1) {
            double r = Math.sqrt(rSquared);
            double psi = r * psi_max + Math.PI;
            double sin_psi = Math.sin(psi);
            double cos_psi = Math.cos(psi);
            double sin_alpha = py / r;
            double cos_alpha = px / r;
            dir = new Vec3(-sin_psi * cos_alpha, sin_psi * sin_alpha, -cos_psi);
        } else {
            dir = new Vec3();
        }

        Vec3 origin3 = DEFAULT_CAMERA_POS;
        Vec4 origin = origin3.asPosition();
        origin = transform.mulVec(origin);

        Vec4 direction = dir.normalize().asDirection();
        direction = transform.mulVec(direction);

        return new Ray(origin.asVec3(), direction.asVec3(), null);
    }
}