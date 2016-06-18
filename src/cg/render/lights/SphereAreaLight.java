package cg.render.lights;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.*;
import cg.render.Light.VisibilityResult;
import cg.render.materials.ColorMaterial;
import cg.render.shapes.Sphere;

/**
 * Created by Hobbit on 6/18/16.
 */
public class SphereAreaLight extends Light {
    private Sphere sphere;

    public SphereAreaLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s, double radius) {
        super(scene, color, intensity, t, r, s);
        sphere = new Sphere(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1), radius);
        sphere.setMaterial(new ColorMaterial(Channel.getBasicColorChannel(color)));
        addChild(sphere);
    }

    @Override
    public boolean isRenderable() {
        return true;
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return sphere.calculateBBox(sphere.transform);
    }

    @Override
    public Collision completeCollision(QuickCollision qc) {
        return sphere.completeCollision(qc);
    }

    @Override
    protected Collision internalCompleteCollision(QuickCollision qc) {
        return sphere.internalCompleteCollision(qc);
    }

    @Override
    public QuickCollision internalQuickCollideWith(Ray ray) {
        return sphere.internalQuickCollideWith(ray);
    }

    @Override
    public Vec3 vectorFromCollision(Collision col) {
        return null;
    }

    @Override
    public boolean visibleFrom(Collision col) {
        return false;
    }
    
    // TODO: Add sampler to sample area light
	@Override
	public VisibilityResult sampledVisibleFrom(Collision col) {
		//TODO: Implement
		return null;
	}
}
