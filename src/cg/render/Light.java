package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

public abstract class Light extends WorldObject {
	public static final float EPSILON = 0.0005f;
	protected Scene scene;
	protected Color color;
	protected float intensity;
	
	public abstract Color illuminateSurface(Collision col);
	
	protected Light(Scene scene, Color color, float intensity, Vec3 t, Vec3 r) {
		this.scene = scene;
		this.intensity = intensity;
		this.color = color;
		setTransform(t, r, null);
	}

	public void setTransform(Vec3 t, Vec3 r, Vec3 s) {
		if (t == null) {
			t = new Vec3();
		}

		if (r == null) {
			r = new Vec3();
		}

		if (s == null) {
			s = new Vec3(1, 1, 1);
		}

		Matrix4 translation = Matrix4.transFromVec(t);
		Matrix4 rot = getRotationMatrix(r);

		this.transform = translation.mul(rot);
		this.invTransform = transform.inverse();
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getIntensity() {
		return intensity;
	}
}