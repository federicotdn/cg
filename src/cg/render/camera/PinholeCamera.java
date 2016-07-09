package cg.render.camera;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.Image;
import cg.render.Ray;

public class PinholeCamera extends Camera {
	private double fovDegrees;
	private double halfImagePlane;
	
	public PinholeCamera(Vec3 t, Vec3 r, double fov) {
		super(t, r);
		this.fovDegrees = fov;
		this.halfImagePlane = Math.tan(Math.toRadians(fovDegrees / 2));
	}

	@Override
	protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
		double aspectRatio = img.aspectRatio();

		double ndcx = (pixelX + offsetX) / img.getWidth();
		double ndcy = (pixelY + offsetY) / img.getHeight();
		
		double px = ((2 * ndcx) - 1) * aspectRatio * halfImagePlane;
		double py = (1 - (2 * ndcy)) * halfImagePlane;
		
		Vec3 origin3 = DEFAULT_CAMERA_POS;
		Vec4 origin = origin3.asPosition();
		origin = transform.mulVec(origin);
		
		Vec3 direction3 = new Vec3((double)px, (double)py, 1);
		Vec4 direction = direction3.normalize().asDirection();
		direction = transform.mulVec(direction);
		
		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
