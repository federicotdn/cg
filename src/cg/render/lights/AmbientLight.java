package cg.render.lights;

import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Scene;

public class AmbientLight extends Light {

	protected AmbientLight(Scene scene, Color color, float intensity) {
		super(scene, color, intensity, null, null);
	}

	@Override
	public Color illuminateSurface(Collision col) {
		// TODO Auto-generated method stub
		return null;
	}

}
