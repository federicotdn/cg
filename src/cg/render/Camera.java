package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.rand.MultiJitteredSampler;

public class Camera extends WorldObject {
	private double fovDegrees;
	
	//TODO: Use WorldObject transform or this one
	private Vec3 pos;
	
	public Camera(Vec3 t, Vec3 r, double fov) {
		setTransform(t, r);
		this.fovDegrees = fov;
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
	
	private Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
		double aspectRatio = img.aspectRatio();
		double halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));
		
		double ndcx = ((double)pixelX + offsetX) / img.getWidth();
		double ndcy = ((double)pixelY + offsetY) / img.getHeight();
		
		double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
		double py = (1 - (2 * ndcy)) * halfImagePlane;
		
		Vec3 origin3 = new Vec3();
		Vec4 origin = origin3.asPosition();
		origin = transform.mulVec(origin);
		
		Vec3 direction3 = new Vec3((double)px, (double)py, 1);
		Vec4 direction = direction3.normalize().asDirection();
		direction = transform.mulVec(direction);
		
		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
