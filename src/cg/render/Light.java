package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

public abstract class Light extends Primitive {
	public static final double EPSILON = 0.001;
	protected Scene scene;
	protected Color color;
	protected double intensity;
	
	public static class VisibilityResult {
		public final boolean isVisible;
		public final Vec3 surfaceToLight;
		public final Color color;

		public VisibilityResult(boolean isVisible, Vec3 surfaceToLight, Color color) {
			this.isVisible = isVisible;
			this.surfaceToLight = surfaceToLight;
			this.color = color;
		}
	}

	public abstract VisibilityResult sampledVisibleFrom(Collision col);

	protected Light(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s) {
		this.scene = scene;
		this.intensity = intensity;
		this.color = color;
		setTransform(t, r, s);
	}

	protected Light(Scene scene, Color color, double intensity, Vec3 t, Vec3 r) {
		this(scene, color, intensity, t, r, null);
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return null;
	}

	@Override
	protected Collision internalCompleteCollision(QuickCollision qc) {
		return null;
	}

	@Override
	protected QuickCollision internalQuickCollideWith(Ray ray) {
		return null;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getIntensity() {
		return intensity;
	}

	public static boolean pointVisibleFrom(Scene scene, Collision col, Vec3 position) {
		Vec3 surfaceToLight = position.sub(col.getPosition());
		double cosAngle = col.getNormal().dot(surfaceToLight.normalize());

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
}