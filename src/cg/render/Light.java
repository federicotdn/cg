package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

public abstract class Light extends Primitive {
	public static final double EPSILON = 0.001;
	protected Scene scene;
	protected Color color;
	protected double intensity;
	
	public abstract boolean visibleFrom(Collision col);

	protected Light(Scene scene, Color color, double intensity, Vec3 t, Vec3 r, Vec3 s) {
		this.scene = scene;
		this.intensity = intensity;
		this.color = color;
		setTransform(t, r, s);
	}

	protected Light(Scene scene, Color color, double intensity, Vec3 t, Vec3 r) {
		this(scene, color, intensity, t, r, new Vec3(1,1,1));
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return null;
	}

	@Override
	public Collision completeCollision(QuickCollision qc) {
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
	
	public abstract Vec3 vectorFromCollision(Collision col);
	
	public double getIntensity() {
		return intensity;
	}
}