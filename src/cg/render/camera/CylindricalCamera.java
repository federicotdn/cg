package cg.render.camera;

import cg.math.Vec3;
import cg.render.Image;
import cg.render.Ray;

public class CylindricalCamera extends Camera {

	public CylindricalCamera(Vec3 t, Vec3 r) {
		super(t, r);
	}

	@Override
	protected Ray rayFor(Image img, int pixelX, int pixelY, double offsetX, double offsetY) {
		return null;
	}

}
