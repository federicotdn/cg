package cg.render;

public abstract class Material {
	public abstract Color getSurfaceColor(Collision col, Scene scene);
}
