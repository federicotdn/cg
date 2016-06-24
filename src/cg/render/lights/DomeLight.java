package cg.render.lights;

import cg.math.MathUtils;
import cg.math.Matrix4;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.materials.ColorMaterial;
import cg.render.shapes.Sphere;

/**
 * Created by Hobbit on 6/23/16.
 */
public class DomeLight extends Light {
    private MultiJitteredSampler.SubSampler sampler;

    public DomeLight(Scene scene, Channel colorChannel, double intensity) {
        super(scene, colorChannel.colorComponent, intensity, null, null, null);
        setMaterial(new ColorMaterial(colorChannel));
        MultiJitteredSampler baseSampler = scene.getSamplerCaches().poll();
        sampler = baseSampler.getSubSampler(10000);
        sampler.generateSamples();
        scene.getSamplerCaches().offer(baseSampler);
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return null;
    }

    @Override
    public boolean isRenderable() {
        return true;
    }

    @Override
    public QuickCollision internalQuickCollideWith(Ray ray) {
        return new QuickCollision(this, ray, null, Double.MAX_VALUE, -1);
    }

    @Override
    public Collision internalCompleteCollision(QuickCollision qc) {
        Ray ray = qc.getLocalRay();
        Vec3 dir = ray.getDirection();

        Vec3 normal = dir.mul(-1);
        Vec2 uvs = Sphere.uvs(normal);

        return new Collision(this, ray, 1, normal, uvs.x, uvs.y);
    }

    private ColorMaterial getColorMaterial() {
        return (ColorMaterial)getMaterial();
    }


    public VisibilityResult sampledVisibleFrom(Collision col) {
        Vec2 sample = sampler.getRandomSample();
        Vec3 position = MathUtils.squareToHemisphere(sample.x, sample.y, 0);

        position = MathUtils.tangentToWorldSpace(position, col.getNormal());

        Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
        Vec3 path = position.sub(displacedOrigin);

        Ray ray = new Ray(displacedOrigin, path, Ray.DEFAULT_MAX_T);
        QuickCollision sceneCol = scene.collideRay(ray);
        boolean visible;
        if (sceneCol != null) {
            visible = false;
        } else {
            visible = true;
        }

        double u = 0.5 + ((Math.atan2(position.z, position.x))/(2*Math.PI));
        double v = 0.5 - (Math.asin(position.y)/Math.PI);

        return new VisibilityResult(visible, position, getColorMaterial().getSampledColor(new Vec2(u, v)).mul(intensity));
    }
        @Override
    public boolean visibleFrom(Collision col) {
       return false;
    }

    @Override
    public Vec3 vectorFromCollision(Collision col) {
        return null;
    }
}
