package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Ray;

/*
 * 
 * "PointLight changes everything. Again." - Steve Jobs, 2015
 * 
 */
public class PointLight extends Light {

	private final float EPSILON = 0.05f;
	private Vec3 position;
	
	public PointLight(Vec3 position) {
		this.position = position;
	}

	@Override
	public Color illuminateSurface(Collision col) {
		Vec3 surfaceToLight = position.sub(col.getPosition());
		float cosAngle = col.getNormal().dot(surfaceToLight.normalize());
		if (cosAngle < 0) {
			return null;
		}

		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(EPSILON));
		Vec3 path = position.sub(displacedOrigin);
		
		Ray ray = new Ray(displacedOrigin, path, path.len() - EPSILON);
		
		Collision sceneCol = scene.collideRay(ray);
		if (sceneCol != null) {
			//if (!sceneCol.shapeName.equals(col.shapeName))
				return null;
		}

		return new Color(cosAngle);
	}
}
