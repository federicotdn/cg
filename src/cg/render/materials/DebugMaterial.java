package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Material;
import cg.render.Scene;

public class DebugMaterial extends Material {

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Vec3 n = col.getNormal();
		return new Color(Math.abs(n.x), Math.abs(n.y), Math.abs(n.z));
	}
}
