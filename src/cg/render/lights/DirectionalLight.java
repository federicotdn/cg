package cg.render.lights;

import cg.math.Vec3;
import cg.math.Vec4;
import cg.render.*;

public class DirectionalLight extends Light {
	private Vec3 negDirection;
	
	public DirectionalLight(Scene scene, Vec3 direction) {
		this(scene, Color.WHITE, 0.4f, direction);
	}
	
	public DirectionalLight(Scene scene, Color color, float intensity, Vec3 r) {
		super(scene, color, intensity, null, r);
		Vec3 direction = getTransform().mulVec(new Vec4(0,0,1, 0)).asVec3();
		this.negDirection = direction.normalize().mul(-1);
	}

	@Override
	public Color illuminateSurface(Collision col) {
		float cosAngle = col.getNormal().dot(negDirection);
		if (cosAngle < 0) {
			return null;
		}
		
		Vec3 displacedOrigin = col.getPosition().sum(col.getNormal().mul(Light.EPSILON));
		Ray ray = new Ray(displacedOrigin, negDirection, null);
		
		if (scene.collideRay(ray) != null) {
			return null;
		}
		
		return col.getPrimitive().getMaterial().getSurfaceColor(col, this, negDirection, scene.getCamera().getPosition());
	}
}
