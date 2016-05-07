package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;
import cg.render.Scene;

public class Phong extends Material {

	private Color specularColor;
	private float exponent;
	
	public Phong(Color color, float offsetU, float offsetV, float scaleU, float scaleV) {
		super(color, offsetU, offsetV, scaleU, scaleV);
	}
	
	public void setPhongProperties(Color specularColor, float exponent) {
		this.specularColor = specularColor;
		this.exponent = exponent;
	}

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color c = new Color(1, 0, 0, 0);
		Color myColor = getTextureColorMix(col.u, col.v);
		
		//WIP
		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				float cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = myColor.mul(cosAngle).mul(light.getColor().mul(light.getIntensity()));
				c = c.sum(result);				
			}
		}
		
		return c;
	}
}
