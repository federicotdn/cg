package cg.render.materials;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;

public class DebugMaterial extends Material {

	public DebugMaterial() {
		super(null, 0, 0, 1, 1);
	}

	@Override
	public Color calculateSurfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos) {
		Vec3 n = col.getNormal();
		return new Color(Math.abs(n.x), Math.abs(n.y), Math.abs(n.z));
	}
}
