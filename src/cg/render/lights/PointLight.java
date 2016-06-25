package cg.render.lights;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.*;
import cg.render.Light.VisibilityResult;

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
	
	public PointLight(Scene scene, Color color, double intensity, Vec3 position) {
		super(scene, color, intensity, position, null);
	}

	@Override
	public void calculateTransform() {
		super.calculateTransform();
		this.position = transform.mulVec(new Vec4(0,0,0,1)).asVec3();
	}

	public boolean visibleFrom(Collision col) {
		if (col.getRay().shouldIgnoreShadows()) {
			return true;
		}
		return pointVisibleFrom(scene, col, position);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}

	@Override
	public VisibilityResult sampledVisibleFrom(Collision col) {
		boolean visible = visibleFrom(col);
		Vec3 surfaceToLight = position.sub(col.getPosition()).normalize();
		return new VisibilityResult(visible, surfaceToLight, color.mul(intensity));
	}
}
