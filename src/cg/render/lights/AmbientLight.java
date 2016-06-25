package cg.render.lights;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Scene;

public class AmbientLight extends Light {

	public AmbientLight(Scene scene, Color color, double intensity) {
		super(scene, color, intensity, null, null);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}

	@Override
	public VisibilityResult sampledVisibleFrom(Collision col) {
		return new VisibilityResult(true, col.getNormal(), color.mul(intensity));
	}
}
