package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;

public class ColorMaterial extends Material {
	public ColorMaterial(Color color, float offsetU, float offsetV, float scaleU, float scaleV) {
		super(color, offsetU, offsetV, scaleU, scaleV);
	}
	
	@Override
	public cg.render.Color calculateSurfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos) {
		return color.sum(l.getColor().mul(l.getIntensity()));
	}
}
