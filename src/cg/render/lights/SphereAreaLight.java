package cg.render.lights;

import cg.math.*;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.materials.EmissiveMaterial;
import cg.render.shapes.Sphere;

/**
 * Created by Hobbit on 6/18/16.
 */
public class SphereAreaLight extends Light {
    private MultiJitteredSampler.SubSampler sampler;
    private Sphere sphere;
    private Vec3 position;

    public SphereAreaLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s, double radius) {
        super(scene, color, intensity, t, r, s);
        double area = (radius * radius) * 4 * Math.PI;
        area = MathUtils.clamp(area, 1, area);
        this.intensity = intensity/area;
        sphere = new Sphere(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1), radius);
        sphere.setMaterial(new EmissiveMaterial(Channel.getBasicColorChannel(color), getIntensity()));
        addChild(sphere);
        MultiJitteredSampler baseSampler = scene.getSamplerCaches().poll();
        sampler = baseSampler.getSubSampler(10000);
        sampler.generateSamples();
        scene.getSamplerCaches().offer(baseSampler);
    }

    @Override
    public void calculateTransform() {
        super.calculateTransform();
        this.position = transform.mulVec(new Vec4(0,0,0,1)).asVec3();
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
        Vec2 sample = sampler.getRandomSample();
        Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, 0);
        Vec3 dir = col.getPosition().sub(position).normalize();
        Vec3 newRayDir = MathUtils.tangentToWorldSpace(hemisphereSample, dir);
        Vec3 surfacePosition = position.sum(newRayDir.mul(sphere.getRadius()));
        VisibilityResult res = new VisibilityResult(pointVisibleFrom(scene, col, surfacePosition), surfacePosition, color.mul(getIntensity()));
        return res;
	}

    @Override
    public Material getMaterial() {
        return sphere.getMaterial();
    }
}
