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

	private Vec3 position;
	
	public PointLight(Vec3 position) {
		this.position = position;
	}

	@Override
	public Color illuminateSurface(Collision col) {
		Vec3 path = position.sub(col.getPosition());
		float cosAngle = col.getNormal().dot(path.normalize());
		if (cosAngle < 0) {
			return null;
		}

		float t = path.len();
		Ray ray = new Ray(col.getPosition().sum(path.mul(0.1f)), path);
		ray.setMaxT(t * 0.1f);
		if (scene.collideRay(ray) != null) {
			return null;
		}

		return new Color(cosAngle);
	}

	//	@Override
//	public Collision visibleFrom(Vec3 origin, Vec3 normal) {
//		Vec3 path = position.sub(origin); // Path from surface to light
//		if (path.dot(normal) < 0) {
//			return null;
//		}
//		float t = path.len();
//		Ray ray = new Ray(origin, path);
//		ray.setMaxT(t);
//		return scene.collideRay(ray);
//	}
}
