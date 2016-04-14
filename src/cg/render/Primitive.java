package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

import java.util.ArrayList;
import java.util.List;

public abstract class Primitive {
	
	protected Matrix4 transform;
	private Matrix4 invTransform;

	private Matrix4 invRotation;
	
	protected List<Primitive> children = new ArrayList<Primitive>();
	protected Primitive parent;
	private BoundingBox bbox;
	
	protected void setTransform(Vec3 t, Vec3 r, Vec3 s) {
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
		Matrix4 rot = rotZ.mul(rotY).mul(rotX);
		Matrix4 scale = Matrix4.scaleFromVec(s);
		
		Matrix4 transform = translation;
		transform = transform.mul(rot).mul(scale);
		
		this.invTransform = transform.inverse();
		this.transform = transform;
		
		this.invRotation = rot.inverse();
		
		this.bbox = calculateBBox(translation, rot, scale);
	}
	
	public Collision collideWith(Ray ray) {
		Vec4 localOrigin = ray.getOrigin().asPosition();
		localOrigin = invTransform.mulVec(localOrigin);
		
		Vec4 localDirection = ray.getDirection().asDirection();
		localDirection = invTransform.mulVec(localDirection);
		
		Ray localRay = new Ray(localOrigin.asVec3(), localDirection.asVec3().normalize());
		Collision localCol = calculateCollision(localRay);
		if (localCol == null) {
			return null;
		}
		
		Vec3 localCollisionPos = localCol.getPosition();
		Vec3 collisionPos = transform.mulVec(localCollisionPos.asPosition()).asVec3();
		
		Vec3 path = collisionPos.sub(ray.getOrigin());
		float t = path.len();

		Vec4 localNormal = localCol.getNormal().asDirection();
		Vec3 worldNormal = invTransform.traspose().mulVec(localNormal).asVec3();
		
		return new Collision(ray, t, worldNormal);
	}

	public BoundingBox getBBox() {
		return bbox;
	}
	
	protected abstract Collision calculateCollision(Ray ray);
	protected abstract BoundingBox calculateBBox(Matrix4 t, Matrix4 r, Matrix4 s);
}
