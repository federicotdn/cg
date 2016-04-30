package cg.render.lights;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.*;

public class SpotLight extends Light {

	private Vec3 position;
	private Vec3 direction;
	private float cosCutoff;
	
	public SpotLight(Scene scene, Color color, float intensity, Vec3 t, Vec3 r, float cutoff) {
		super(scene, color, intensity, t, r);
		this.position = t;
		this.direction = getTransform().mulVec(new Vec4(0, 0, 1, 0)).asVec3();

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
