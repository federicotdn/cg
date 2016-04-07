package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

public class Camera {
	private float fovDegrees;
	private Matrix4 transform;
	
	public Camera(Vec3 pos, Vec3 rotation, float fov) {
		transform = Matrix4.transFromVec(pos);
		//TODO: add rotation matrices
		this.fovDegrees = fov;
	}
	
	public Ray rayFor(Image img, int pixelX, int pixelY) {
		float aspectRatio = img.aspectRatio();
		double halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));
		
		double ndcx = ((double)pixelX + 0.5) / img.getWidth();
		double ndcy = ((double)pixelY + 0.5) / img.getHeight();

		double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
		double py = (1 - (2 * ndcy)) * halfImagePlane;

		Vec3 origin3 = new Vec3();
		Vec4 origin = origin3.asPosition();
		origin = origin.mul(transform);
		
		Vec3 direction3 = new Vec3((float)px, (float)py, -1);
		Vec4 direction = direction3.asDirection();
		direction = direction.mul(transform);

		return new Ray(origin.toVec3(), direction.toVec3().normalize());
	}
}
