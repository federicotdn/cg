package cg.render.shapes;

import cg.math.Matrix4;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

public class Mesh extends Primitive {

	@Override
	protected Collision calculateCollision(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BoundingBox calculateBBox(Matrix4 trs) {
		return null;
	}
}
