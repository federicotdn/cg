package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;
import cg.math.Vec4;

public abstract class Primitive extends WorldObject {
	private Material material;
	private BoundingBox bbox;
	private String name;
	
	public Collision collideWith(Ray ray) {
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

		Material mat = localCol.getPrimitive().getMaterial();
		float u = (localCol.u % mat.getScaleU()) / mat.getScaleU();
		float v = (localCol.v % mat.getScaleV()) / mat.getScaleV();
		return new Collision(localCol.getPrimitive(), ray, t, worldNormal, u, v);
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
	
	protected abstract Collision calculateCollision(Ray ray);
	protected abstract BoundingBox calculateBBox(Matrix4 trs);
}
