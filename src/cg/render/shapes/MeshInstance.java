package cg.render.shapes;

import cg.math.Matrix4;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;
import cg.render.assets.Mesh;

public class MeshInstance extends Primitive {

	/*
	 * MeshInstance acts as an "instance" for Mesh objects,
	 * which contain the actual geometry data.
	 */
	
	private final Mesh meshData;
	
	public MeshInstance(Mesh meshData) {
		this.meshData = meshData;
	}
	
	@Override
	protected Collision calculateCollision(Ray ray) {
		return meshData.calculateCollision(ray);
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return meshData.getBBox().transformBBox(trs);
	}
}
