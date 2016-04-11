package cg.render;

public abstract class Light {
	protected Scene scene;
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}