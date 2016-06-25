package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.assets.Texture;

/**
 * Created by Hobbit on 6/24/16.
 */
public class CookTorranceMaterial extends Material {
    private static final double ior = 3.5;

    private final Diffuse diffuse;

    //specular channel
    private final Color specularColor;
    private final Texture specularColorTexture;
    private final Vec2 specularColorTextureOffset;
    private final Vec2 specularColorTextureScale;

    //roughness channel
    private final double roughness;
    private final Texture roughnessTexture;
    private final Vec2 roughnessTextureOffset;
    private final Vec2 roughnessTextureScale;

    public CookTorranceMaterial(Channel colorChannel, Channel specularColorChannel, Channel roughnessChannel) {
        diffuse = new Diffuse(colorChannel);

        this.specularColor = specularColorChannel.colorComponent;
        if (specularColorChannel.isTextured()) {
            this.specularColorTexture = specularColorChannel.getTexture();
            this.specularColorTextureOffset = specularColorChannel.textureOffset;
            this.specularColorTextureScale = specularColorChannel.textureScale;
        } else {
            this.specularColorTexture = null;
            this.specularColorTextureOffset = null;
            this.specularColorTextureScale = null;
        }

        this.roughness = roughnessChannel.scalarComponent;
        if (roughnessChannel.isTextured()) {
            this.roughnessTexture = roughnessChannel.getTexture();
            this.roughnessTextureOffset = roughnessChannel.textureOffset;
            this.roughnessTextureScale = roughnessChannel.textureScale;
        } else {
            this.roughnessTexture = null;
            this.roughnessTextureOffset = null;
            this.roughnessTextureScale = null;
        }
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        return null;
    }

    @Override
    public PathData traceSurfaceColor(Collision col, Scene scene) {

        if (col.getRay().getHops() > scene.getMaxTraceDepth() || Math.random() < Scene.ROULETTE_P) {
            return new PathData(Scene.BACKGROUND_COLOR);
        }

        // Direct Color

        Color diffuseColor = scene.BACKGROUND_COLOR;
        Color cookTorrance = scene.BACKGROUND_COLOR;

        Color specularTexColor = getSpecularColor(col.u, col.v);
        double roughnessTex = getFinalRoughness(col.u, col.v);

        if (scene.getLights().size() > 0) {
            int index = (int) Math.random() * scene.getAreaLights().size();
            Light light = scene.getLights().get(index);
            Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
            Color result = (light.getColor().mul(light.getIntensity())).mul(diffuse.brdf(surfaceToLight, col));
            diffuseColor = diffuseColor.sum(result);

            result = light.getColor().mul(light.getIntensity()).mul(brdf(surfaceToLight, col, roughnessTex));
            cookTorrance = cookTorrance.sum(result);
        }

        Light light = null;
        if (scene.getAreaLights().size() > 0) {
            int index = (int) Math.random() * scene.getAreaLights().size();
            light = scene.getAreaLights().get(index);
            Light.VisibilityResult visibility = light.sampledVisibleFrom(col);
            if (visibility.isVisible) {
                Vec3 surfaceToLight = visibility.lightSurface.sub(col.getPosition()).normalize();
                Color result = visibility.color.mul(diffuse.brdf(surfaceToLight, col));
                diffuseColor = diffuseColor.sum(result);

                result = visibility.color.mul(brdf(surfaceToLight, col, roughnessTex));
                cookTorrance = cookTorrance.sum(result);
            }
        }

        diffuseColor = diffuseColor.mul(diffuse.getColor(col.u, col.v));
        Color directColor = diffuseColor.sum(specularTexColor.mul(cookTorrance));

        // Indirect Lightning

        Vec3 reflectionDir = col.getRay().getDirection().reflect(col.getNormal());
        Vec3 newRayDir = sample(scene, reflectionDir, roughnessTex);

        Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
        QuickCollision qc = scene.collideRay(newRay);
        if (qc != null && !qc.getPrimitive().equals(light )) {
            Collision newCol = qc.completeCollision();
            PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
            Color indirectColor = pd.color.mul(brdf(newRayDir, col, roughnessTex)).mul(2);

            Color diffuseIndirect = indirectColor.mul(diffuse.brdf(newRayDir, col)).mul(diffuse.getColor(col.u, col.v));
            Color specularIndirect = indirectColor.mul(brdf(newRayDir, col, roughnessTex)).mul(specularTexColor);

            pd.color = directColor.sum(diffuseIndirect.sum(specularIndirect));
            pd.distance += newCol.getPosition().sub(col.getPosition()).len();
            return pd;
        }

        PathData pd = new PathData(directColor);
        return pd;
    }

    public double brdf(Vec3 l, Collision col, double roughness) {
        Vec3 v = col.getRay().getDirection().mul(-1);
        Vec3 h = v.sum(l);
        h = h.div(h.len());

        Vec3 normal = col.getNormal();
        double normalDotH = normal.dot(h);
        double vdotH = v.dot(h);
        double g1 = (2 * normalDotH * (normal.dot(v)))/vdotH;
        double g2 = (2 * normalDotH * normal.dot(l))/vdotH;

        double g = Math.min(1, Math.min(g1, g2));

        double exp = ((normalDotH * normalDotH) - 1) / ((roughness * roughness) * (normalDotH * normalDotH));
        double d = (1/(Math.PI * roughness * roughness * Math.pow(normalDotH, 4))) * Math.exp(exp);

        double cosI = normal.dot(v);
        double n = 1/ior;
        double sen2t = (n * n) * (1 - (cosI * cosI));
        double f = MathUtils.shlick(1, ior, cosI, sen2t);

        return (f * d * g) / (4 * normal.dot(l) * cosI);

    }

    private double getFinalRoughness(double u, double v) {
        double roughnessTex = roughness;
        if (roughnessTexture != null) {
            Color texColor = roughnessTexture.getOffsetScaledSample(roughnessTextureOffset, roughnessTextureScale, u, v);
            roughnessTex *= texColor.getRed(); // Assuming image is grayscale
        }

        return roughnessTex;
    }

    public Vec3 sample(Scene scene, Vec3 direction, double roughness) {
        MultiJitteredSampler sampler = scene.getSamplerCaches().poll();
        Vec2 sample = sampler.getRandomSample();
        scene.getSamplerCaches().offer(sampler);

        Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, roughness).normalize();
        Vec3 newRayDir = MathUtils.tangentToWorldSpace(hemisphereSample, direction);

        return newRayDir;
    }

    private Color getSpecularColor(double u, double v) {
        Color specularTexColor = specularColor;
        if (specularColorTexture != null) {
            Color texColor = specularColorTexture.getOffsetScaledSample(specularColorTextureOffset, specularColorTextureScale, u, v);
            specularTexColor = specularTexColor.mul(texColor);
        }

        return specularTexColor;
    }


}
