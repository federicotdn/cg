package cg.render.camera;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.rand.MultiJitteredSampler;
import cg.render.Image;
import cg.render.Ray;
import cg.render.WorldObject;

public abstract class Camera extends WorldObject {	
	protected static Vec3 DEFAULT_CAMERA_POS = new Vec3(0, 0, 0);
	private Vec3 pos;
	
	public Camera(Vec3 t, Vec3 r) {
		setTransform(t, r);
	}
	
	public void setTransform(Vec3 t, Vec3 r) {
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
	
	public Vec3 getPosition() {
		return pos;
	}
	
	@Override
	public void calculateTransform() {
		super.calculateTransform();
		this.pos = transform.mulVec(new Vec4(0,0,0,1)).asVec3();
	}
	
	public void raysFor(Ray[] rays, MultiJitteredSampler sampler, Image img, int pixelX, int pixelY) {
		final int size = sampler.getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double offsetX = sampler.xCoords[size * j + i];
				double offsetY = sampler.yCoords[size * j + i];

				rays[size * j + i] = rayFor(img, pixelX, pixelY, offsetX, offsetY);
			}
		}
	}
	
	public void raysFor(Ray[] rays, MultiJitteredSampler.SubSampler subSampler, Image img, int pixelX, int pixelY) {
		final int size = subSampler.sampleCount();
		for (int i = 0; i < size; i++) {
			double offsetX = subSampler.xCoords[i];
			double offsetY = subSampler.yCoords[i];
			rays[i] = rayFor(img, pixelX, pixelY, offsetX, offsetY);
		}
	}
	
	protected abstract Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY);
}
