package cg.render;

import cg.math.Vec3;

public abstract class Light {
	protected Scene scene;
	
	public abstract Color illuminateSurface(Collision origin);
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}