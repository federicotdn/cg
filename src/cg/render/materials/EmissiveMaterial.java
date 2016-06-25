package cg.render.materials;

import cg.math.Vec2;
import cg.parser.Channel;
import cg.render.*;
import cg.render.assets.Texture;

/**
 * Created by Hobbit on 6/21/16.
 */
public class EmissiveMaterial extends Material {
    private final Color color;
    private double intensity;
    private final Texture colorTexture;
    private final Vec2 colorTextureOffset;
    private final Vec2 colorTextureScale;

    public EmissiveMaterial(Channel colorChannel, double intensity) {
        this.color = colorChannel.colorComponent;
        this.intensity = intensity;
        if (colorChannel.isTextured()) {
            this.colorTexture = colorChannel.getTexture();
            this.colorTextureOffset = colorChannel.textureOffset;
            this.colorTextureScale = colorChannel.textureScale;
        } else {
            this.colorTexture = null;
            this.colorTextureOffset = null;
            this.colorTextureScale = null;
        }
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        return getSampledColor(col.u, col.v);
    }

    @Override
    public PathData traceSurfaceColor(Collision col, Scene scene) {
        PathData pd = new PathData(getSampledColor(col.u, col.v));
        return pd;
    }

    public Color getSampledColor(double u, double v) {
        Color myColor = color;

        if (colorTexture != null) {
            Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, u, v);
            myColor = myColor.mul(texCol);
        }
        return myColor.mul(intensity);
    }
}
