package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Ray;
import cg.render.Scene;

public class SpotLight extends Light {

	private Vec3 position;
	private Vec3 direction;
	private float cosCutoff;
	
	public SpotLight(Scene scene, Color color, float intensity, Vec3 position, Vec3 direction, float cutoff) {
		super(scene, color, intensity);
		this.position = position;
		this.direction = direction.normalize();

		if (Math.abs(cutoff) > 90) {
			cutoff = 90;
		}
		this.cosCutoff = (float) Math.cos(Math.toRadians(Math.abs(cutoff)));
	}

	@Override
	public Color illuminateSurface(Collision col) {
		Vec3 surfaceToLight = position.sub(col.getPosition());
		float cosAngle = col.getNormal().dot(surfaceToLight.normalize());
		
		if (cosAngle < 0) {
			return null;
		}
		
		Vec3 lightToSurface = surfaceToLight.mul(-1).normalize();
		if (lightToSurface.dot(direction) < cosCutoff) {
			return null;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Vec3 path = position.sub(displacedOrigin);
		
		Ray ray = new Ray(displacedOrigin, path, path.len() - Light.EPSILON);
		if (scene.collideRay(ray) != null) {
			return null;
		}
		
		return col.getPrimitive().getMaterial().surfaceColor(col, this, surfaceToLight, scene.getCamera().getPosition());
	}
}
