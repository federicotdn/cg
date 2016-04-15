package cg.render;

public abstract class Light {

	protected Scene scene;

	public abstract Color illuminateSurface(Collision col);
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}