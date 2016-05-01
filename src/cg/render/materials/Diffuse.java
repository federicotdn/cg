package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;

public class Diffuse extends Material {
	public static final Diffuse DIFFUSE_DEFAULT = new Diffuse(Color.WHITE);
	
	private Color color;
	
	public Diffuse(Color color) {
		this.color = color;
	}
	
	@Override
	public Color surfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos) {
		float cosAngle = surfaceToLight.normalize().dot(col.getNormal());
		Color c = color.sum(l.getColor().mul(l.getIntensity()));
		return c.mul(cosAngle);
	}

}
