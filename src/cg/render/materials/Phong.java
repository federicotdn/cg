package cg.render.materials;

import cg.math.Vec3;
import cg.render.*;

public class Phong extends Material {
	private Color specularColor;
	private double exponent;

	public Phong(Color color, double offsetU, double offsetV, double scaleU, double scaleV, Color specularColor, double exponent) {
		super(color, offsetU, offsetV, scaleU, scaleV);
		this.specularColor = specularColor;
		this.exponent = exponent;
	}


	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color diffuse = scene.BACKGROUND_COLOR;
		Color diffuseTexColor = getTextureColorMix(col.u, col.v);

		Color specular = scene.BACKGROUND_COLOR;

		//WIP
		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = (light.getColor().mul(light.getIntensity())).mul(cosAngle);
				diffuse = diffuse.sum(result);

				Vec3 r = surfaceToLight.reflect(col.getNormal());
				double spec = Math.pow(Math.max(0, - r.dot(col.getRay().getDirection())), 20);
				result = (new Color(spec).mul(light.getColor().mul(light.getIntensity())));
				specular = specular.sum(result);
			}
		}

		return diffuse.mul(diffuseTexColor).sum(specularColor.mul(specular));
	}
}
