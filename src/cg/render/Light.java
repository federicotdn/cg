package cg.render;

import cg.math.Vec3;

public abstract class Light {
	protected Scene scene;
	
	public abstract Collision visibleFrom(Vec3 origin, Vec3 normal);
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}