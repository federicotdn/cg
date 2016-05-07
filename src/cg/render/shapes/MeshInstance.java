package cg.render.shapes;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.QuickCollision;
import cg.render.Ray;
import cg.render.assets.Mesh;

public class MeshInstance extends Primitive {

	/*
	 * MeshInstance acts as an "instance" for Mesh objects,
	 * which contain the actual geometry data.
	 */
	
	private final Mesh meshData;
	
	public MeshInstance(Mesh meshData, Vec3 t, Vec3 r, Vec3 s) {
		this.meshData = meshData;
		setTransform(t,r,s);
	}
	
	@Override
	protected QuickCollision calculateCollision(Ray ray) {
		return meshData.calculateCollision(ray, this);
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return meshData.getBBox().transformBBox(trs);
	}

	public Mesh getMeshData() {
		return meshData;
	}

	@Override
	public Collision getFullCollision(QuickCollision qc) {
		
		return new Collision(this, qc.getLocalRay(), qc.getWorldT(), new Vec3(), 0, 0);
	}
}
