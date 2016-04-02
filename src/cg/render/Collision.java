package cg.render;

public class Collision {
	private float t;
	private Ray ray;
	
	public Collision(Ray ray, float t) {
		this.ray = ray;
		this.t = t;
	}
	
	public float getT() {
		return t;
	}
}
