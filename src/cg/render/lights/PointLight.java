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
	public Color illuminateSurface(Collision origin) {		
		Vec3 path = position.sub(origin.getPosition()).normalize(); // Path from surface to light
		float cosAngle = path.dot(origin.getNormal());
		System.out.println(cosAngle);
		if (cosAngle < 0) {
			return Color.BLACK;
		}
		
		float t = path.len();
		Ray ray = new Ray(origin.getPosition().sum(path.mul(0.95f)), path);
		ray.setMaxT(t * 0.95f);

		if (scene.collideRay(ray) == null) {
			return new Color(cosAngle);
		}
		
		return Color.BLACK;
	}
}
