package cg.render;

public abstract class Light {
	public static final float EPSILON = 0.0005f;
	protected Scene scene;
	
	public abstract Color illuminateSurface(Collision col);
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}