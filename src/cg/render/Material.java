package cg.render;

public abstract class Material {
	public abstract Color getSurfaceColor(Collision col, Scene scene);
	public abstract PathData traceSurfaceColor(Collision col, Scene scene);

	public boolean isEmissive() {
		return false;
	}
}
