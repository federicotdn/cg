package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Ray;

public class DirectionalLight extends Light {
	private Vec3 negDirection;
	
	public DirectionalLight(Vec3 direction) {
		this.negDirection = direction.normalize().mul(-1);
	}

	@Override
	public Color illuminateSurface(Collision col) {
		float cosAngle = col.getNormal().dot(negDirection);
		if (cosAngle < 0) {
			return null;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Ray ray = new Ray(displacedOrigin, negDirection, null);
		
		if (scene.collideRay(ray) != null) {
			return null;
		}
		
		return new Color(cosAngle);
	}
}
