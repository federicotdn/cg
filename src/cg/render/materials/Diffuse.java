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
			VisibilityResult visibilityResult = light.sampledVisibleFrom(col);
			if (visibilityResult.isVisible) {
				Vec3 surfaceToLight = visibilityResult.surfaceToLight;
				Color result = visibilityResult.color.mul(brdf(surfaceToLight, col));
				c = c.sum(result);				
			}
		}

		return c.mul(getColor(col.u, col.v));
	}

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
		// Direct Lightning 
		Color c = Color.BLACK;

		if (col.getRay().getHops() > scene.getMaxTraceDepth() || Math.random() < Scene.ROULETTE_P) {
			return new PathData(Scene.BACKGROUND_COLOR);
		}

		Light light = null;
		if (scene.getLights().size() > 0) {
			int index = (int) Math.random() * scene.getLights().size();
			light = scene.getLights().get(index);
			VisibilityResult visibility = light.sampledVisibleFrom(col);
			if (visibility.isVisible) {
				Vec3 surfaceToLight = visibility.surfaceToLight;
				Color result = visibility.color.mul(brdf(surfaceToLight, col));
				c = c.sum(result);
			}
		}

		// Indirect Lightning

		Vec3 newRayDir = sample(scene, col);
		Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
		QuickCollision qc = scene.collideRay(newRay);
		if (qc != null && !qc.getPrimitive().equals(light )) {
			Collision newCol = qc.completeCollision();
			PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
			Color indirectColor = pd.color.mul(brdf(newRayDir, col)).mul(2 * Math.PI);
			pd.color = indirectColor.sum(c).mul(getColor(col.u, col.v));
			pd.distance += newCol.getPosition().sub(col.getPosition()).len();
			return pd;
		}

		PathData pd = new PathData(c.mul(getColor(col.u, col.v)));
		return pd;
	}

	public Color getColor(double u, double v) {
		Color myColor = color;
		if (colorTexture != null) {
			Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, u, v);
			myColor = myColor.mul(texCol);
		}

		return myColor;
	}

	public double brdf(Vec3 dir, Collision col) {
		return dir.dot(col.getNormal())/Math.PI;
	}

	public Vec3 sample(Scene scene, Collision col) {
		MultiJitteredSampler sampler = scene.getSamplerCaches().poll();
		Vec2 sample = sampler.getRandomSample();
		scene.getSamplerCaches().offer(sampler);

		Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, 0).normalize();
		Vec3 newRayDir = MathUtils.tangentToWorldSpace(hemisphereSample, col.getNormal());

		return newRayDir;
	}
}
