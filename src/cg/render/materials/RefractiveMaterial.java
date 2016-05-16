package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec3;
import cg.render.*;

/**
 * Created by Hobbit on 5/12/16.
 */
public class RefractiveMaterial extends Material {
    private double ior;
    private Color refractiveColor;

    public RefractiveMaterial(Color color, double offsetU, double offsetV, double scaleU,
                              double scaleV, Color refractiveColor, double ior) {
        super(color, offsetU, offsetV, scaleU, scaleV);
        this.refractiveColor = refractiveColor;
        this.ior = ior;
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        Color refractedColor = Color.BLACK;
        Ray ray = col.getRay();

        Vec3 normal = col.getNormal();
        double n1 = 1;
        double n2 = ior;
        if (ray.isInsidePrimitive()) {
            normal = normal.mul(-1);
            n1 = ior;
            n2 = 1;
        }

        Vec3 dir = ray.getDirection();

        double n = n1/n2;
        double cosI = - normal.dot(dir);
        double sen2t = (n * n) * (1 - (cosI * cosI));
        Vec3 refraction = dir.mul(n).sub(normal.mul((n * cosI) + Math.sqrt(1 - sen2t)));

        double r;
        if (sen2t > 1) {
            r = 1;
        } else {
            double r0 = Math.pow((n1 - n2)/(n1 + n2), 2);
            double cos;
            if (n1 <= n2) {
                cos = - normal.dot(ray.getDirection());
            } else {
                cos = Math.sqrt(1 - sen2t);
            }

            r = r0 + ((1 - r0)*(Math.pow(1 - cos, 5)));
        }
        r = MathUtils.clamp(r);
        if (ray.getHops() <= scene.getRefractionTraceDepth()) {
            if (sen2t <= 1) {
                Ray refractionRay = new Ray(col.getPosition().sum(normal.mul(-0.00001)), refraction, Double.POSITIVE_INFINITY, ray.getHops() + 1, !ray.isInsidePrimitive(), true);
                QuickCollision qc = scene.collideRay(refractionRay);
                if (qc != null) {
                    Collision refractionCol = qc.completeCollision();
                    refractedColor = refractionCol.getPrimitive().getMaterial().getSurfaceColor(refractionCol, scene).mul(refractiveColor);
                }
            }
        }

        Color reflectedColor = scene.BACKGROUND_COLOR;
        if (ray.getHops() <= scene.getReflectionTraceDepth()) {
            Vec3 d = col.getRay().getDirection().mul(-1);
            Vec3 reflection = d.reflect(col.getNormal());

            Ray reflectionRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.00001)), reflection, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
            QuickCollision qc =  scene.collideRay(reflectionRay);
            if (qc != null) {
                Collision reflectionCol = qc.completeCollision();
                Color reflectionColor = reflectionCol.getPrimitive().getMaterial().getSurfaceColor(reflectionCol, scene);
                reflectedColor = color.mul(reflectionColor);
            }
        }

        return refractedColor.mul(1-r).sum(reflectedColor.mul(r));
    }
}
