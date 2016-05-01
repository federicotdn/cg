package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;

public class ColorMaterial extends Material {

	private final Color color;
	
	public ColorMaterial(Color color) {
		this.color = color;
	}
	
	@Override
	public cg.render.Color surfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos) {
		return color.sum(l.getColor().mul(l.getIntensity()));
	}
}
