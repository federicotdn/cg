package cg.render.camera;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.Image;
import cg.render.Ray;

public class CylindricalCamera extends Camera {

	private double height;
	
	public CylindricalCamera(Vec3 t, Vec3 r, double height) {
		super(t, r);
		this.height = height;
	}

	@Override
	protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {		
		double ndcx = (pixelX + offsetX) / img.getWidth();
		double ndcy = (pixelY + offsetY) / img.getHeight();
		
		double px = (2 * ndcx) - 1;
		double py = (2 * ndcy) - 1;

		double phi = Math.PI * px;
		
		Vec3 dir = new Vec3(Math.sin(phi), -py * height, Math.cos(phi));
		
		Vec3 origin3 = DEFAULT_CAMERA_POS;
		Vec4 origin = origin3.asPosition();
		origin = transform.mulVec(origin);
		
		Vec4 direction = dir.normalize().asDirection();
		direction = transform.mulVec(direction);
		
		return new Ray(origin.asVec3(), direction.asVec3(), null);
	}
}
