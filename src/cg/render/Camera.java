package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

public class Camera {
	private float fovDegrees;
	private Matrix4 transform;
	private Vec3 pos;
	
	public Camera(Vec3 pos, Vec3 rotation, float fov) {
		Matrix4 rotX = Matrix4.rotationX(rotation.x);
		Matrix4 rotY = Matrix4.rotationY(rotation.y);
		Matrix4 rotZ = Matrix4.rotationZ(rotation.z);
		Matrix4 rot = rotZ.mul(rotY).mul(rotX);
		
		this.pos = pos;
		this.transform = Matrix4.transFromVec(pos).mul(rot);
		this.fovDegrees = fov;
	}
	
	public Vec3 getPosition() {
		return pos;
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
		origin = transform.mulVec(origin);
		
		Vec3 direction3 = new Vec3((float)px, (float)py, 1);
		Vec4 direction = direction3.normalize().asDirection();
		direction = transform.mulVec(direction);

		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
