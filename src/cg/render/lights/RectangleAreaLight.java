package cg.render.lights;

import cg.math.MathUtils;
import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.materials.EmissiveMaterial;
import cg.render.shapes.FinitePlane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hobbit on 6/17/16.
 */
public class RectangleAreaLight extends Light {
    private FinitePlane plane;
    private double[] xSamples;
    private double[] ySamples;
    private Vec3 normal;

    public RectangleAreaLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s, double width, double height) {
        super(scene, color, intensity, t, r, s);
        double area = width * height;
        this.intensity = intensity/area;

        plane = new FinitePlane(width, height, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1), new Vec3(0,0,1));
        plane.setMaterial(new EmissiveMaterial(Channel.getBasicColorChannel(color), getIntensity()));
        addChild(plane);
        MultiJitteredSampler baseSampler = scene.getSamplerCaches().poll();
        MultiJitteredSampler.SubSampler sampler = baseSampler.getSubSampler(SAMPLES);
        sampler.generateSamples();
        scene.getSamplerCaches().offer(baseSampler);

        if (Math.abs(height - width) < MathUtils.EPSILON) {
            xSamples = sampler.xCoords;
            multiplySamples(width, xSamples);
            ySamples = sampler.yCoords;
            multiplySamples(width, ySamples);
        } else {
            double[][] samples;
            if (width > height) {
                samples = samplesForSize(height, width, sampler.yCoords, sampler.xCoords);
                xSamples = samples[1];
                ySamples = samples[0];
            } else {
                samples = samplesForSize(width, height, sampler.xCoords, sampler.yCoords);
                xSamples = samples[0];
                ySamples = samples[1];
            }
        }
    }

    @Override
    public void calculateTransform() {
        super.calculateTransform();
        normal = invTransform.traspose().mulVec(new Vec4(0, 0, 1, 1)).asVec3().normalize();
    }

    @Override
    protected BoundingBox calculateBBox(Matrix4 trs) {
        return plane.calculateBBox(plane.transform);
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
	public VisibilityResult sampledVisibleFrom(Collision col) {
        int index = (int)(Math.random() * xSamples.length);
        Vec3 pos = new Vec3(xSamples[index], ySamples[index], 0);
        pos = transform.mulVec(pos.asPosition()).asVec3();

        boolean visible;
        double finalIntensity = 0;
        Vec3 lightToSurface = col.getPosition().sub(pos).normalize();
        if (normal.dot(lightToSurface) < 0) {
            visible = false;
        } else {
            visible = pointVisibleFrom(scene, col, pos);
            double cosAngle = MathUtils.clamp(lightToSurface.dot(normal));
            finalIntensity = intensity * cosAngle;
        }

        Vec3 surfaceToLight = pos.sub(col.getPosition()).normalize();
        
        return new VisibilityResult(visible, surfaceToLight, color.mul(finalIntensity));
	}

    private double[][] samplesForSize(double size, double multiplier, double[] samples, double[] secondSamples) {
        List<Double> newSamples = new ArrayList<>();
        List<Double> newSecondSamples = new ArrayList<>();
        for (int i =0;  i < samples.length; i++) {
            double newSample = samples[i] * multiplier;
            if (newSample <= size) {
                newSamples.add(newSample);
                newSecondSamples.add(secondSamples[i] * multiplier);
            }
        }

        double[] newSampleArray = new double[newSamples.size()];
        double[] newSecondSampleArray = new double[newSamples.size()];
        for (int i =0;  i < newSampleArray.length; i++) {
            newSampleArray[i] = newSamples.get(i);
            newSecondSampleArray[i] = newSecondSamples.get(i);
        }

        return new double[][] {newSampleArray, newSecondSampleArray};
    }

    private void multiplySamples(double multiplier, double[] samples) {
        for (int i =0;  i < samples.length; i++) {
            samples[i] *= multiplier;
        }
    }

    @Override
    public Material getMaterial() {
        return plane.getMaterial();
    }
}
