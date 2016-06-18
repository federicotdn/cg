package cg.render.lights;

import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Scene;
import cg.render.materials.ColorMaterial;
import cg.render.shapes.Sphere;

/**
 * Created by Hobbit on 6/18/16.
 */
public class SphereAreaLight extends Light {
    private Sphere sphere;

    public SphereAreaLight(Scene scene, Color color, double intensity, Vec3 r, Vec3 t, Vec3 s, double radius) {
        super(scene, color, intensity, t, r, s);
        sphere = new Sphere(t, r, s, radius);
        sphere.setMaterial(new ColorMaterial(Channel.getBasicColorChannel(color)));
    }

    @Override
    public void calculateTransform() {
        super.calculateTransform();
        sphere.setParent(this);
        sphere.calculateTransform();;
    }

    @Override
    public Vec3 vectorFromCollision(Collision col) {
        return null;
    }

    // TODO: Add sampler to sample area light
    @Override
    public boolean visibleFrom(Collision col) {
        return false;
    }
}
