package cg.render.materials;

import cg.math.Vec3;
import cg.render.*;

/**
 * Created by Hobbit on 5/7/16.
 */
public class ReflectiveMaterial extends Material {

    public ReflectiveMaterial(Color color, double offsetU, double offsetV, double scaleU, double scaleV) {
        super(color, offsetU, offsetV, scaleU, scaleV);
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        if (col.getRay().getHops() > scene.getReflectionTraceDepth()) {
           return scene.BACKGROUND_COLOR;
        }

        Vec3 d = col.getRay().getDirection().mul(-1);
        Vec3 reflection = d.reflect(col.getNormal());

        Ray reflectionRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.001)), reflection, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
        Collision reflectionCol =  scene.collideRay(reflectionRay);
        if (reflectionCol != null) {
            Color reflectionColor = reflectionCol.getPrimitive().getMaterial().getSurfaceColor(reflectionCol, scene);
            return color.mul(reflectionColor);
        }

        return scene.BACKGROUND_COLOR;
    }
}
