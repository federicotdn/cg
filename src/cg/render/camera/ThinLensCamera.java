package cg.render.camera;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.rand.MultiJitteredSampler;
import cg.render.Image;
import cg.render.Ray;
import cg.render.Scene;
import cg.render.assets.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hobbit on 7/8/16.
 */
public class ThinLensCamera extends Camera {
    private double fovDegrees;
    private double[] xSamples;
    private double[] ySamples;
    private double halfImagePlane;
    private double focusDistance;
    private double aperture;
    private Texture tex;


    public ThinLensCamera(Vec3 t, Vec3 r, double fov, double focusDistance, double aperture, Scene scene, Texture tex) {
        super(t, r);
        this.fovDegrees = fov;
        this.halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));
        this.focusDistance = focusDistance;
        this.aperture = aperture;

        this.tex = tex;
        if (tex != null) {
            generateSamples(scene, tex);
        }
    }

    @Override
    protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
        double aspectRatio = img.aspectRatio();

        double ndcx = (pixelX + 0.5) / img.getWidth();
        double ndcy = (pixelY + 0.5) / img.getHeight();

        double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
        double py = (1 - (2 * ndcy)) * halfImagePlane;

        Vec2 diskSample;
        if (tex == null) {
            diskSample = MathUtils.squareToDisk(offsetX, offsetY);
        } else {
            if (1 - tex.getSample(offsetX, offsetY).getRed() > 0.00001) {
                int index = (int) (Math.random() * xSamples.length);
                offsetX = xSamples[index];
                offsetY = ySamples[index];
            }
            diskSample = new Vec2(offsetX, offsetY);
        }

        diskSample = diskSample.mul(aperture);
        Vec3 origin3 = new Vec3(diskSample.x, diskSample.y, 0);

        Vec3 direction3 = new Vec3(px, py, 1).normalize();
        double ft = focusDistance / direction3.z;
        Vec3 pfocus = direction3.mul(ft);
        direction3 = pfocus.sub(origin3).normalize();

        Vec4 direction = direction3.asDirection();
        direction = transform.mulVec(direction);
        direction3 = direction.asVec3();

        Vec4 origin = origin3.asPosition();
        origin = transform.mulVec(origin);
        origin3 = origin.asVec3();

        return new Ray(origin3, direction3, null);
    }

    private void generateSamples(Scene scene, Texture tex) {
        MultiJitteredSampler baseSampler = scene.getSamplerCaches().poll();
        MultiJitteredSampler.SubSampler sampler = baseSampler.getSubSampler(Scene.SAMPLERS_SIZE);
        sampler.generateSamples();
        scene.getSamplerCaches().offer(baseSampler);

        List<Double> xCoords = new ArrayList<>();
        List<Double> yCoords = new ArrayList<>();
        for (int i = 0; i < sampler.xCoords.length; i++) {
            double x = sampler.xCoords[i];
            double y = sampler.yCoords[i];

            if (1 - tex.getSample(x, y).getRed() < 0.000001) {
                xCoords.add(x);
                yCoords.add(y);
            }
        }

        xSamples = new double[xCoords.size()];
        for (int i = 0; i < xSamples.length; i++) {
            xSamples[i] = xCoords.get(i);
        }

        ySamples = new double[yCoords.size()];
        for (int i = 0; i < ySamples.length; i++) {
            ySamples[i] = yCoords.get(i);
        }
    }
}
