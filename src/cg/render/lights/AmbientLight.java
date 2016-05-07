package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Scene;

public class AmbientLight extends Light {

	public AmbientLight(Scene scene, Color color, float intensity) {
		super(scene, color, intensity, null, null);
	}

	@Override
	public boolean visibleFrom(Collision col) {
		return true;
	}

	@Override
	public Vec3 vectorFromCollision(Collision col) {
		return col.getNormal();
	}
}
