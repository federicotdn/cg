package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

import java.util.ArrayList;
import java.util.List;

public abstract class Primitive {	
	private Matrix4 invTransform = new Matrix4();
	private Matrix4 transform;
	private List<Primitive> children = new ArrayList<Primitive>();
	private Primitive parent;
	
	public void setTransform(Vec3 t, Vec3 r, Vec3 s) {
		if (t == null) {
			t = new Vec3();
		}
		
		if (r == null) {
			r = new Vec3();
		}
		
		if (s == null) {
			s = new Vec3(1, 1, 1);
		}
		
		Matrix4 translation = Matrix4.transFromVec(t);
		Matrix4 rotX = Matrix4.rotationX(r.x);
		Matrix4 rotY = Matrix4.rotationY(r.y);
		Matrix4 rotZ = Matrix4.rotationZ(r.z);
		Matrix4 scale = Matrix4.scaleFromVec(s);
		
		Matrix4 transform = translation;
		transform = transform.mult(rotZ).mult(rotY).mult(rotX).mult(scale);
		invTransform = transform.inverse();
		this.transform = transform;
	}
	
	public Collision collideWith(Ray ray) {
		Vec4 localOrigin = ray.getOrigin().asPosition();
		localOrigin = localOrigin.mul(invTransform);
		
		Vec4 localDirection = ray.getDirection().asDirection();
		localDirection = localDirection.mul(invTransform);
		
		return calculateCollision(new Ray(localOrigin.toVec3(), localDirection.toVec3().normalize()));
	}
	
	protected abstract Collision calculateCollision(Ray ray);
}
