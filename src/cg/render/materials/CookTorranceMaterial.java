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
    private static final double ior = 3.0504;

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

        Color diffuseColor = Scene.BACKGROUND_COLOR;
        Color cookTorrance = Scene.BACKGROUND_COLOR;

        Color specularTexColor = getSpecularColor(col.u, col.v);
        double roughnessTex = getFinalRoughness(col.u, col.v);

        Light light = null;
        if (scene.getLights().size() > 0) {
            int index = (int) Math.random() * scene.getLights().size();
            light = scene.getLights().get(index);
            Light.VisibilityResult visibility = light.sampledVisibleFrom(col);
            if (visibility.isVisible) {
                Vec3 surfaceToLight = visibility.surfaceToLight;
                Color result = visibility.color.mul(diffuse.brdf(surfaceToLight, col));
                diffuseColor = diffuseColor.sum(result);

                result = visibility.color.mul(brdf(surfaceToLight, col, roughnessTex));
                cookTorrance = cookTorrance.sum(result);
            }
        }

        diffuseColor = diffuseColor.mul(diffuse.getColor(col.u, col.v));
        Color directColor = diffuseColor.sum(specularTexColor.mul(cookTorrance));

        // Indirect Lightning

        Vec3 reflectionDir = col.getRay().getDirection().mul(-1).reflect(col.getNormal());
        Vec3 newRayDir = sample(scene, reflectionDir, roughnessTex);

        Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
        QuickCollision qc = scene.collideRay(newRay);
        if (qc != null && !qc.getPrimitive().equals(light)) {
            Collision newCol = qc.completeCollision();
            PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);

            Color indirectColor = pd.color;
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
        Vec3 normal = col.getNormal();
        double normalDotL = normal.dot(l);

        if (normalDotL > 0) {
            Vec3 v = col.getRay().getDirection().mul(-1);
            Vec3 h = v.sum(l).normalize();

            double normalDotH = Math.abs(normal.dot(h));
            double vdotH =  MathUtils.clamp(v.dot(h));
            double cosI = normal.dot(v);
            double g1 = (2 * normalDotH * cosI)/vdotH;
            double g2 = (2 * normalDotH * normalDotL)/vdotH;

            double g = Math.min(1, Math.min(g1, g2));

            double exp = ((normalDotH * normalDotH) - 1) / ((roughness * roughness) * (normalDotH * normalDotH));
            double d = (1/(Math.PI * roughness * roughness * Math.pow(normalDotH, 4))) * Math.exp(exp);
            double f = MathUtils.schlick(ior, cosI);


            double ans =  (f * d * g) / (Math.PI * normalDotL * cosI);

            return MathUtils.clamp(ans * normalDotL);
        }

        return 0;
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

        double exp = (2/(roughness * roughness)) - 2;
        Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, exp);
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
