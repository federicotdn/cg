package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.Light.VisibilityResult;
import cg.render.assets.Texture;

public class Diffuse extends Material {
	public static final Diffuse DEFAULT_DIFFUSE = new Diffuse(Channel.getDefaultColorChannel());
	
	//New fields:
	private final Color color;
	private final Texture colorTexture;
	private final Vec2 colorTextureOffset;
	private final Vec2 colorTextureScale;
	
	public Diffuse(Channel colorChannel) {
		this.color = colorChannel.colorComponent;
		if (colorChannel.isTextured()) {
			this.colorTexture = colorChannel.getTexture();
			this.colorTextureOffset = colorChannel.textureOffset;
			this.colorTextureScale = colorChannel.textureScale;
		} else {
			this.colorTexture = null;
			this.colorTextureOffset = null;
			this.colorTextureScale = null;			
		}
	}

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color c = new Color(1, 0, 0, 0);

		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = (light.getColor().mul(light.getIntensity())).mul(cosAngle);
				c = c.sum(result);
			}
		}

		Color myColor = color;
		if (colorTexture != null) {
			Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, col.u, col.v);
			myColor = myColor.mul(texCol);
		}
		return myColor.mul(c);
	}

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
		// Direct Lightning 
		Color c = Color.BLACK;

		if (col.getRay().getHops() > scene.getMaxTraceDepth()) {
			return new PathData(Color.BLACK);
		}

		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = (light.getColor().mul(light.getIntensity())).mul(cosAngle);
				c = c.sum(result);
			}
		}
		
		for (Light light : scene.getAreaLights()) {
			VisibilityResult visibility = light.sampledVisibleFrom(col);
			if (visibility.isVisible) {
				Vec3 surfaceToLight = visibility.lightSurface.sub(col.getPosition()).normalize();
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = (light.getColor().mul(light.getIntensity())).mul(cosAngle);
				c = c.sum(result);
			}
		}

		Color myColor = color;
		if (colorTexture != null) {
			Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, col.u, col.v);
			myColor = myColor.mul(texCol);
		}

		c = c.mul(myColor);
		// Indirect Lightning
		
		MultiJitteredSampler sampler = scene.getSamplerCaches().poll();
		Vec2 sample = sampler.getRandomSample();
		scene.getSamplerCaches().offer(sampler);

		Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, 0).normalize();

		Vec3 normal = col.getNormal();

		Vec3 nt = normal.getSmallestAxis().cross(normal).normalize();
		Vec3 nb = nt.cross(normal).normalize();
		Vec3 newRayDir = new Vec3(hemisphereSample.x * nt.x + hemisphereSample.y * normal.x + hemisphereSample.z * nb.x,
				hemisphereSample.x * nt.y + hemisphereSample.y * normal.y + hemisphereSample.z * nb.y,
				hemisphereSample.x * nt.z + hemisphereSample.y * normal.z + hemisphereSample.z * nb.z).normalize();

		Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
		QuickCollision qc = scene.collideRay(newRay);
		if (qc != null) {
			Collision newCol = qc.completeCollision();
			PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
			double cosAngle = newRayDir.dot(col.getNormal());
			Color indirectColor = pd.color.mul(cosAngle).mul(myColor);
			pd.color = indirectColor.sum(c);
			return pd;
		}

		PathData pd = new PathData(c);
		return pd;
	}
}
