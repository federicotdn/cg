package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;
import cg.rand.MultiJitteredSampler;

public class Camera extends WorldObject {
	private float fovDegrees;
	private Matrix4 transform;
	private Vec3 pos;
	
	public Camera(Vec3 pos, Vec3 rotation, float fov) {
		Matrix4 rot = getRotationMatrix(rotation);
		
		this.pos = pos;
		this.transform = Matrix4.transFromVec(pos).mul(rot);
		this.fovDegrees = fov;
	}
	
	public Vec3 getPosition() {
		return pos;
	}
	
	public void raysFor(Ray[] rays, MultiJitteredSampler sampler, Image img, int pixelX, int pixelY) {		
		if (sampler == null) {
			rays[0] = rayFor(img, pixelX, pixelY, 0.5f, 0.5f);
			return;
		}
		
		final int size = sampler.getSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				float offsetX = sampler.xCoords[size * j + i];
				float offsetY = sampler.yCoords[size * j + i];

				rays[size * j + i] = rayFor(img, pixelX, pixelY, offsetX, offsetY);
			}
		}
	}
	
	private Ray rayFor(Image img, int pixelX, int pixelY, float offsetX, float offsetY) {
		float aspectRatio = img.aspectRatio();
		double halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));
		
		double ndcx = ((double)pixelX + offsetX) / img.getWidth();
		double ndcy = ((double)pixelY + offsetY) / img.getHeight();
		
		double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
		double py = (1 - (2 * ndcy)) * halfImagePlane;
		
		Vec3 origin3 = new Vec3();
		Vec4 origin = origin3.asPosition();
		origin = transform.mulVec(origin);
		
		Vec3 direction3 = new Vec3((float)px, (float)py, 1);
		Vec4 direction = direction3.normalize().asDirection();
		direction = transform.mulVec(direction);
		
		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
