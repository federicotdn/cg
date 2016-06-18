package cg.render.lights;

import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Scene;
import cg.render.materials.ColorMaterial;
import cg.render.shapes.FinitePlane;

/**
 * Created by Hobbit on 6/17/16.
 */
public class RectangleAreaLight extends Light {
    private FinitePlane plane;

    public RectangleAreaLight(Scene scene, Color color, double intensity, Vec3 r, Vec3 t, Vec3 s, double width, double height) {
        super(scene, color, intensity, t, r, s);
        plane = new FinitePlane(width, height, new Vec3(0,0,0), new Vec3(0,0,0), new Vec3(1,1,1));
        plane.setMaterial(new ColorMaterial(Channel.getBasicColorChannel(color)));
    }

    @Override
    public void calculateTransform() {
        super.calculateTransform();
        plane.setParent(this);
        plane.calculateTransform();;
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
