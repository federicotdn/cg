package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
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

    public PathData traceSurfaceColor(Collision col, Scene scene) {
        // Direct Lightning
        Color c = Color.BLACK;

        if (col.getRay().getHops() > scene.getMaxTraceDepth() || Math.random() < Scene.ROULETTE_P) {
            return new PathData(Scene.BACKGROUND_COLOR);
        }

        if (scene.getLights().size() > 0) {
            int index = (int) Math.random() * scene.getAreaLights().size();
            Light light = scene.getLights().get(index);
            Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
            Color result = (light.getColor().mul(light.getIntensity())).mul(brdf(surfaceToLight, col));
            c = c.sum(result);
        }

        Light light = null;
        if (scene.getAreaLights().size() > 0) {
            int index = (int) Math.random() * scene.getAreaLights().size();
            light = scene.getAreaLights().get(index);
            Light.VisibilityResult visibility = light.sampledVisibleFrom(col);
            if (visibility.isVisible) {
                Vec3 surfaceToLight = visibility.lightSurface.sub(col.getPosition()).normalize();
                Color result = visibility.color.mul(brdf(surfaceToLight, col));
                c = c.sum(result);
            }
        }

        // Indirect Lightning

        Vec3 newRayDir = sample(scene, col);
        Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
        QuickCollision qc = scene.collideRay(newRay);
        if (qc != null && !qc.getPrimitive().equals(light )) {
            Collision newCol = qc.completeCollision();
            PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
            Color indirectColor = pd.color.mul(brdf(newRayDir, col)).mul(2 * Math.PI);
            pd.color = indirectColor.sum(c).mul(getColor(col.u, col.v));
            pd.distance += newCol.getPosition().sub(col.getPosition()).len();
            return pd;
        }

        PathData pd = new PathData(c.mul(getColor(col.u, col.v)));
        return pd;
    }

    public double brdf(Collision col, Vec3 l) {
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


}
