package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Ray;
import cg.render.Scene;

/*
 * 
 * "PointLight changes everything. Again." - Steve Jobs, 2015
 * 
 */
public class PointLight extends Light {
	private Vec3 position;
	
	public PointLight(Scene scene, Vec3 position) {
		this(scene, Color.WHITE, 0.01f, position);
	}
	
	public PointLight(Scene scene, Color color, float intensity, Vec3 position) {
		super(scene, color, intensity);
		this.position = position;
	}

	@Override
	public Color illuminateSurface(Collision col) {
		Vec3 surfaceToLight = position.sub(col.getPosition());
		float cosAngle = col.getNormal().dot(surfaceToLight.normalize());
		
		if (cosAngle < 0) {
			return null;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Vec3 path = position.sub(displacedOrigin);
		
		Ray ray = new Ray(displacedOrigin, path, path.len() - Light.EPSILON);
		
		Collision sceneCol = scene.collideRay(ray);
		if (sceneCol != null) {
			return null;
		}

		return col.getPrimitive().getMaterial().surfaceColor(col, this, surfaceToLight, scene.getCamera().getPosition());
	}
}
