package cg.render.materials;

import cg.parser.Channel;
import cg.render.*;

/**
 * Created by Hobbit on 6/21/16.
 */
public class EmissiveMaterial extends Material {
    private final Color color;
    private double intensity;

    public EmissiveMaterial(Channel colorChannel, double intensity) {
        this.color = colorChannel.colorComponent;
        this.intensity = intensity;
    }

    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
        return color.mul(intensity);
    }

    @Override
    public PathData traceSurfaceColor(Collision col, Scene scene) {
        PathData pd = new PathData(color.mul(intensity));
        return pd;
    }
}
