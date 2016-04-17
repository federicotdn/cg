package cg.render;

import cg.math.Vec3;

public abstract class Material {
	public abstract Color surfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos);
}
