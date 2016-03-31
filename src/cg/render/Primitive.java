package cg.render;

import java.util.Optional;

public abstract class Primitive {	
	//TODO: add translation, rotation and scale
	
	public Optional<Collision> collideWith(Ray ray) {
		return calculateCollision(ray);
	}
	
	protected abstract Optional<Collision> calculateCollision(Ray ray);
}
