package cg.render;

public abstract class Light {
	public static final float EPSILON = 0.0005f;
	protected Scene scene;
	protected Color color;
	protected float intensity;
	
	public abstract Color illuminateSurface(Collision col);
	
	protected Light(Scene scene, Color color, float intensity) {
		this.scene = scene;
		this.intensity = intensity;
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getIntensity() {
		return intensity;
	}
}