package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

public abstract class Primitive extends WorldObject {
	private Material material;
	private BoundingBox bbox;
	private String name;
	
	public QuickCollision collideWith(Ray ray) {
		Vec4 localOrigin = ray.getOrigin().asPosition();
		localOrigin = invTransform.mulVec(localOrigin);
		
		Vec4 localDirection = ray.getDirection().asDirection();
		localDirection = invTransform.mulVec(localDirection);
		
		Float localMaxT = null;
		if (ray.getMaxT() != Ray.DEFAULT_MAX_T) {
			Vec4 localPath = ray.getDirection().mul(ray.getMaxT()).asDirection();
			localPath = invTransform.mulVec(localPath);
			localMaxT = localPath.asVec3().len();			
		}
		
		Ray localRay = new Ray(localOrigin.asVec3(), localDirection.asVec3().normalize(), localMaxT);
		QuickCollision localCol = calculateCollision(localRay);
		if (localCol == null) {
			return null;
		}
		
//		Vec3 localCollisionPos = localCol.getPosition();
//		Vec3 collisionPos = transform.mulVec(localCollisionPos.asPosition()).asVec3();
//		
//		Vec3 path = collisionPos.sub(ray.getOrigin());
//		float t = path.len();
//
//		Vec4 localNormal = localCol.getNormal().asDirection();
//		Vec3 worldNormal = invTransform.traspose().mulVec(localNormal).asVec3();
//
//		Material mat = localCol.getPrimitive().getMaterial();
//		float u = ((localCol.u * mat.getScaleU()) + mat.getOffsetU());
//		float v = ((localCol.v * mat.getScaleV()) + mat.getOffsetV());
//		u = repeatUV(u);
//		v = repeatUV(v);

		Vec3 localCollisionPos = ray.runDistance(localCol.getLocalT());
		Vec3 collisionPos = transform.mulVec(localCollisionPos.asPosition()).asVec3();
		Vec3 path = collisionPos.sub(ray.getOrigin());
				
		//return new Collision(localCol.getPrimitive(), ray, t, worldNormal, u, v);

		return new QuickCollision(this, localRay, ray, localCol.getLocalT(), path.len());
	}
	
	public Collision completeCollision(QuickCollision qc) {
		if (qc.getPrimitive() != this) {
			throw new RuntimeException("Error: attempted to complete a QuickCollision with a different Primitive.");
		}

		Collision localCol = getFullCollision(qc);
		Vec4 localNormal = localCol.getNormal().asDirection();
		Vec3 worldNormal = invTransform.traspose().mulVec(localNormal).asVec3();

		float u = ((localCol.u * material.getScaleU()) + material.getOffsetU());
		float v = ((localCol.v * material.getScaleV()) + material.getOffsetV());
		u = repeatUV(u);
		v = repeatUV(v);
		
		return new Collision(this, qc.getWorldRay(), qc.getWorldT(), worldNormal, u , v);
	}

	private float repeatUV(float coord) {
		if (coord >= 0) {
			return coord % 1;
		} else {
			return 1 - (Math.abs(coord) % 1);
		}
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
	
	protected abstract QuickCollision calculateCollision(Ray ray);
	protected abstract Collision getFullCollision(QuickCollision qc);
	protected abstract BoundingBox calculateBBox(Matrix4 trs);
}
