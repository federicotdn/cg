package cg.render.lights;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.materials.ColorMaterial;
import cg.render.shapes.FinitePlane;

/**
 * Created by Hobbit on 6/17/16.
 */
public class RectangleAreaLight extends Light {
    private FinitePlane plane;
    private MultiJitteredSampler.SubSampler sampler;
    public RectangleAreaLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s, double width, double height) {
        super(scene, color, intensity, t, r, s);
        plane = new FinitePlane(width, height, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        plane.setMaterial(new ColorMaterial(Channel.getBasicColorChannel(color)));
    }

    @Override
    public void calculateTransform() {
        super.calculateTransform();
        plane.setParent(this);
        plane.calculateTransform();;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return plane.calculateBBox(trs);
    }

    @Override
    public Collision completeCollision(QuickCollision qc) {
        return plane.completeCollision(qc);
    }

    @Override
    protected Collision internalCompleteCollision(QuickCollision qc) {
        return plane.internalCompleteCollision(qc);
    }

    @Override
    public QuickCollision internalQuickCollideWith(Ray ray) {
        return plane.internalQuickCollideWith(ray);
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
