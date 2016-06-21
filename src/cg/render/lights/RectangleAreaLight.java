package cg.render.lights;

import cg.math.MathUtils;
import cg.math.Matrix4;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.materials.ColorMaterial;
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
    private double area;

    public RectangleAreaLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s, double width, double height) {
        super(scene, color, intensity, t, r, s);
        plane = new FinitePlane(width, height, new Vec3(0, 0, 0), new Vec3(-90, 0, 0), new Vec3(1, 1, 1));
        plane.setMaterial(new ColorMaterial(Channel.getBasicColorChannel(color)));
        addChild(plane);
        MultiJitteredSampler baseSampler = scene.getSamplerCaches().poll();
        MultiJitteredSampler.SubSampler sampler = baseSampler.getSubSampler(10000);
        sampler.generateSamples();
        scene.getSamplerCaches().offer(baseSampler);

        area = width * height;
        area = MathUtils.clamp(area, 1, area);

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
    public Vec3 vectorFromCollision(Collision col) {
        return null;
    }

    // TODO: Add sampler to sample area light
    @Override
    public boolean visibleFrom(Collision col) {
    	return false;
    }
    
	@Override
	public VisibilityResult sampledVisibleFrom(Collision col) {
        int index = (int)(Math.random() * xSamples.length);
        Vec3 pos = new Vec3(xSamples[index], ySamples[index], 0);
        pos = transform.mulVec(pos.asPosition()).asVec3();
        boolean visible = pointVisibleFrom(scene, col, pos);

        return new VisibilityResult(visible, pos);
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
    public double getIntensity() {
        return intensity/area;
    }
}
