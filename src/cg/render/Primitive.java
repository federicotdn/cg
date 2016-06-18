package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

public abstract class Primitive extends WorldObject {
	private Material material;
	private BoundingBox bbox;
	private String name;
	
	public QuickCollision quickCollideWith(Ray ray) {
		Vec4 localOrigin = ray.getOrigin().asPosition();
		localOrigin = invTransform.mulVec(localOrigin);
		
		Vec4 localDirection = ray.getDirection().asDirection();
		localDirection = invTransform.mulVec(localDirection);
		
		Double localMaxT = null;
		if (ray.getMaxT() != Ray.DEFAULT_MAX_T) {
			Vec4 localPath = ray.getDirection().mul(ray.getMaxT()).asDirection();
			localPath = invTransform.mulVec(localPath);
			localMaxT = localPath.asVec3().len();			
		}

		Ray localRay = new Ray(localOrigin.asVec3(), localDirection.asVec3().normalize(), localMaxT, ray.getHops(), ray.isInsidePrimitive(), ray.shouldIgnoreShadows());
		QuickCollision qc = internalQuickCollideWith(localRay);
		if (qc == null) {
			return null;
		}
		
		Vec3 localCollisionPos = qc.getLocalPosition();
		Vec3 collisionPos = transform.mulVec(localCollisionPos.asPosition()).asVec3();
		
		Vec3 path = collisionPos.sub(ray.getOrigin());
		double worldT = path.len();
		
		QuickCollision worldQc = new QuickCollision(this, localRay, ray, qc.getLocalT(), worldT);
		worldQc.copyMeshData(qc);
		return worldQc;
	}
	

	public Collision completeCollision(QuickCollision qc) {
//		if (qc.getPrimitive() != this) {
//			throw new RuntimeException("Error: tried to complete a collision from another primitive.");
//		}
		Collision collision = internalCompleteCollision(qc);
		
		Vec4 localNormal = collision.getNormal().asDirection();
		Vec3 worldNormal = invTransform.traspose().mulVec(localNormal).asVec3();

		double u = collision.u;
		double v = collision.v;
		
		return new Collision(this, qc.getWorldRay(), qc.getWorldT(), worldNormal, u, v);
	}

	public boolean isRenderable() {
		return true;
	}

	@Override
	public void calculateTransform() {
		super.calculateTransform();
		this.bbox = calculateBBox(transform);
	}

	public BoundingBox getBBox() {
		return bbox;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected abstract QuickCollision internalQuickCollideWith(Ray ray);
	protected abstract Collision internalCompleteCollision(QuickCollision qc);
	protected abstract BoundingBox calculateBBox(Matrix4 trs);
}
