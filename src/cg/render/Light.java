package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

public abstract class Light extends WorldObject {
	public static final double EPSILON = 0.00001;
	protected Scene scene;
	protected Color color;
	protected double intensity;
	
	public abstract boolean visibleFrom(Collision col);
	
	protected Light(Scene scene, Color color, double intensity, Vec3 t, Vec3 r) {
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

		Matrix4 translation = Matrix4.transFromVec(t);
		Matrix4 rot = getRotationMatrix(r);

		this.transform = translation.mul(rot);
		this.invTransform = transform.inverse();
	}
	
	public Color getColor() {
		return color;
	}
	
	public abstract Vec3 vectorFromCollision(Collision col);
	
	public double getIntensity() {
		return intensity;
	}
}