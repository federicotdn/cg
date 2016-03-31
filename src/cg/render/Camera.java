package cg.render;

import cg.math.Vec3;

public class Camera {
	private float fov;
	//TODO: add translation and rotation
	
	public Camera(float fov) {
		this.fov = fov;
	}
	
	public Ray rayFor(Image img, int pixelX, int pixelY) {
		float aspectRatio = img.aspectRatio();
		double halfImagePlane = Math.tan(Math.toRadians(fov / 2));
		
		double ndcx = ((double)pixelX + 0.5) / img.getWidth();
		double ndcy = ((double)pixelY + 0.5) / img.getHeight();

		double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
		double py = (1 - (2 * ndcy)) * halfImagePlane;

		Vec3 origin = new Vec3();
		//Vec3 origin = new Vec3(0, 0, -2); //uncomment to make it work without having camera translation
		Vec3 direction = (new Vec3((float)px, (float)py, -1)).normalize();

		return new Ray(origin, direction);
	}
}
