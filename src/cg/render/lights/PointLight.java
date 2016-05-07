package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.QuickCollision;
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
		super(scene, color, intensity, position, null);
		this.position = position;
	}

	@Override
	public boolean visibleFrom(Collision col) {
		Vec3 surfaceToLight = vectorFromCollision(col);
		float cosAngle = col.getNormal().dot(surfaceToLight.normalize());
		
		if (cosAngle < 0) {
			return false;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Vec3 path = position.sub(displacedOrigin);
		
		Ray ray = new Ray(displacedOrigin, path, path.len() - Light.EPSILON);
		
		QuickCollision sceneCol = scene.collideRay(ray);
		if (sceneCol != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public Vec3 vectorFromCollision(Collision col) {
		return position.sub(col.getPosition());
	}
}
