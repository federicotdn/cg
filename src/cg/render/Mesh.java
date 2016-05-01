package cg.render;

public class Mesh {
	
	/*
	 * The Mesh class contains the actual geometry data used in MeshInstance.
	 * Mesh is NOT a Primitive.
	 */

	public Collision calculateCollision(Ray ray) {
		// TODO: Implement collision for non-transformed mesh
		return null;
	}

	public BoundingBox getBBox() {
		// TODO: Implement BBox for non-transformed mesh
		return null;
	}
}
