package cg.render.materials;

import cg.math.Vec3;
import cg.render.*;

/**
 * Created by Hobbit on 5/12/16.
 */
public class RefractiveMaterial extends Material {
    private float ior;
    private Color refractiveColor;

    public RefractiveMaterial(Color color, float offsetU, float offsetV, float scaleU,
                              float scaleV, Color refractiveColor, float ior) {
        super(color, offsetU, offsetV, scaleU, scaleV);
        this.refractiveColor = refractiveColor;
        this.ior = ior;
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        if (col.getRay().getHops() > scene.getRefractionTraceDepth()) {
            return scene.BACKGROUND_COLOR;
        }

        Ray ray = col.getRay();
        Vec3 dir = ray.getDirection();

        Vec3 normal = col.getNormal();
        float n1 = 1;
        float n2 = 1;
        if (ray.isInsidePrimitive()) {
            normal = normal.mul(-1);
            n1 = ior;
            n2 = 1;
        }

        float n = n1/n2;
        float cosI = - normal.dot(dir);
        float sen2t = (n * n) * (1 - (cosI * cosI));
        Vec3 refraction = dir.mul(n).sub(normal.mul((n * cosI) + (float)Math.sqrt(1 - sen2t)));
        Color refractedColor = Color.BLACK;
        Ray refractionRay = new Ray(col.getPosition().sum(normal.mul(-0.05f)), refraction, Float.POSITIVE_INFINITY, ray.getHops() + 1, !ray.isInsidePrimitive());
        Collision refractionCol = scene.collideRay(refractionRay);
        if (refractionCol != null) {
            refractedColor = refractionCol.getPrimitive().getMaterial().getSurfaceColor(refractionCol, scene).mul(refractiveColor);
        }

        return refractedColor;
    }
}
