package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;
import cg.render.Scene;

public class Diffuse extends Material {
	public static final Diffuse DIFFUSE_DEFAULT = new Diffuse(Color.WHITE, 0, 0, 1, 1);

	public Diffuse(Color color, double offsetU, double offsetV, double scaleU, double scaleV) {
		super(color, offsetU, offsetV, scaleU, scaleV);
	}

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color c = new Color(1, 0, 0, 0);
		Color myColor = getTextureColorMix(col.u, col.v);
		
		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = myColor.mul(cosAngle).mul(light.getColor().mul(light.getIntensity()));
				c = c.sum(result);
			}
		}
		
		return c;
	}
}
