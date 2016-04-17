package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Ray;
import cg.render.Scene;

public class DirectionalLight extends Light {
	private Vec3 negDirection;
	
	public DirectionalLight(Scene scene, Vec3 direction) {
		this(scene, Color.WHITE, 0.4f, direction);
	}
	
	public DirectionalLight(Scene scene, Color color, float intensity, Vec3 direction) {
		super(scene, color, intensity);
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
		
		return col.getPrimitive().getMaterial().surfaceColor(col, this, negDirection, scene.getCamera().getPosition());
	}
}
