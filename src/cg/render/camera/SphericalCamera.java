package cg.render.camera;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.Image;
import cg.render.Ray;

public class SphericalCamera extends Camera {
	public SphericalCamera(Vec3 t, Vec3 r) {
		super(t, r);
	}

	@Override
	protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
		double ndcx = (pixelX + offsetX) / img.getWidth();
		double ndcy = (pixelY + offsetY) / img.getHeight();
		
		double px = (2 * ndcx);
		double py = (2 * ndcy) - 1;
		
		double lambda = px * Math.PI;
		double psi = py * 0.5 * Math.PI;
		
		double theta = (0.5 * Math.PI) - psi;
		double phi = Math.PI - lambda;
		
		Vec3 dir = new Vec3(-Math.sin(theta) * Math.sin(phi), -Math.cos(theta),Math.sin(theta) * Math.cos(phi));
		
		Vec3 origin3 = DEFAULT_CAMERA_POS;
		Vec4 origin = origin3.asPosition();
		origin = transform.mulVec(origin);
		
		Vec4 direction = dir.normalize().asDirection();
		direction = transform.mulVec(direction);
		
		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
