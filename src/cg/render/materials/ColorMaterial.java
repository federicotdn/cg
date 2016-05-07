package cg.render.materials;

import cg.render.Collision;
import cg.render.Color;
import cg.render.Material;
import cg.render.Scene;

public class ColorMaterial extends Material {
	public ColorMaterial(Color color, float offsetU, float offsetV, float scaleU, float scaleV) {
		super(color, offsetU, offsetV, scaleU, scaleV);
	}

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		return getTextureColorMix(col.u, col.v);
	}
}
