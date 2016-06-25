package cg.render.lights;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.*;
import cg.render.Light.VisibilityResult;

public class SpotLight extends Light {

	private Vec3 position;
	private Vec3 direction;
	private double cosCutoff;
	
	public SpotLight(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, double cutoff) {
		super(scene, color, intensity, t, r);

		if (Math.abs(cutoff) > 90) {
			cutoff = 90;
		}
		this.cosCutoff = (double) Math.cos(Math.toRadians(Math.abs(cutoff)));
	}

	@Override
	public void calculateTransform() {
		super.calculateTransform();
		this.position = transform.mulVec(new Vec4(0, 0, 0, 1)).asVec3();
		this.direction = transform.mulVec(new Vec4(0, 0, 1, 0)).asVec3();
	}

	public boolean visibleFrom(Collision col) {
		if (col.getRay().shouldIgnoreShadows()) {
			return true;
		}

		Vec3 surfaceToLight = position.sub(col.getPosition());
		double cosAngle = col.getNormal().dot(surfaceToLight.normalize());
		
		if (cosAngle < 0) {
			return false;
		}
		
		Vec3 lightToSurface = surfaceToLight.mul(-1).normalize();
		if (lightToSurface.dot(direction) < cosCutoff) {
			return false;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Vec3 path = position.sub(displacedOrigin);
		
		Ray ray = new Ray(displacedOrigin, path, path.len() - Light.EPSILON);
		if (scene.collideRay(ray) != null) {
			return false;
		}
		
		return true;
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
