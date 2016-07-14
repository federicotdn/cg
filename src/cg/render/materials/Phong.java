package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.rand.MultiJitteredSampler;
import cg.render.*;
import cg.render.Light.VisibilityResult;
import cg.render.assets.Texture;

public class Phong extends Material {	
	//New fields:
	private final Diffuse diffuse;
	
	//specular channel
	private final Color specularColor;
	private final Texture specularColorTexture;
	private final Vec2 specularColorTextureOffset;
	private final Vec2 specularColorTextureScale;
	
	//exponent channel
	private final double exponent;
	private final Texture exponentTexture;
	private final Vec2 exponentTextureOffset;
	private final Vec2 exponentTextureScale;

	public Phong(Channel colorChannel, Channel specularColorChannel, Channel exponentChannel, Channel normalChannel) {
		super(normalChannel);
		diffuse = new Diffuse(colorChannel, null);
		
		this.specularColor = specularColorChannel.colorComponent;
		if (specularColorChannel.isTextured()) {
			this.specularColorTexture = specularColorChannel.getTexture();
			this.specularColorTextureOffset = specularColorChannel.textureOffset;
			this.specularColorTextureScale = specularColorChannel.textureScale;
		} else {
			this.specularColorTexture = null;
			this.specularColorTextureOffset = null;
			this.specularColorTextureScale = null;			
		}
		
		this.exponent = exponentChannel.scalarComponent;
		if (exponentChannel.isTextured()) {
			this.exponentTexture = exponentChannel.getTexture();
			this.exponentTextureOffset = exponentChannel.textureOffset;
			this.exponentTextureScale = exponentChannel.textureScale;
		} else {
			this.exponentTexture = null;
			this.exponentTextureOffset = null;
			this.exponentTextureScale = null;			
		}
	}
	
	private double getFinalExponent(double u, double v) {
		double exponentTex = exponent;
		if (exponentTexture != null) {
			Color texColor = exponentTexture.getOffsetScaledSample(exponentTextureOffset, exponentTextureScale, u, v);
			exponentTex *= texColor.getRed(); // Assuming image is grayscale
		}
		
		return exponentTex;
	}
	
	private Color getSpecularColor(double u, double v) {
		Color specularTexColor = specularColor;
		if (specularColorTexture != null) {
			Color texColor = specularColorTexture.getOffsetScaledSample(specularColorTextureOffset, specularColorTextureScale, u, v);
			specularTexColor = specularTexColor.mul(texColor);
		}
		
		return specularTexColor;
	}
	
	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color diffuseColor = Scene.BACKGROUND_COLOR;
		Color specular = Scene.BACKGROUND_COLOR;
		
		Color specularTexColor = getSpecularColor(col.u, col.v);
		double exponentTex = getFinalExponent(col.u, col.v);

		for (Light light : scene.getLights()) {
			VisibilityResult visibilityResult = light.sampledVisibleFrom(col);
			if (visibilityResult.isVisible) {
				Vec3 surfaceToLight = visibilityResult.surfaceToLight;
				Color result = visibilityResult.color.mul(diffuse.brdf(surfaceToLight, col));
				diffuseColor = diffuseColor.sum(result);

				result = (new Color(brdf(surfaceToLight, col, exponentTex)).mul(light.getColor().mul(light.getIntensity())));
				specular = specular.sum(result);
			}
		}

		diffuseColor = diffuseColor.mul(diffuse.getColor(col.u, col.v));
		return diffuseColor.sum(specularTexColor.mul(specular));
	}

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
		
		if (col.getRay().getHops() > scene.getMaxTraceDepth() || Math.random() < Scene.ROULETTE_P) {
			return new PathData(Scene.BACKGROUND_COLOR);
		}
		
		// Direct Color
		
		Color diffuseColor = Scene.BACKGROUND_COLOR;
		Color specular = Scene.BACKGROUND_COLOR;
		
		Color specularTexColor = getSpecularColor(col.u, col.v);
		double exponentTex = getFinalExponent(col.u, col.v);

		Light light = scene.getRandomLight();
		if (light != null) {
			VisibilityResult visibility = light.sampledVisibleFrom(col);
			if (visibility.isVisible) {
				Vec3 surfaceToLight = visibility.surfaceToLight;
				Color result = visibility.color.mul(diffuse.brdf(surfaceToLight, col));
				diffuseColor = diffuseColor.sum(result);

				result = (new Color(brdf(surfaceToLight, col, exponentTex)).mul(visibility.color));
				specular = specular.sum(result);
			}
		}

		diffuseColor = diffuseColor.mul(diffuse.getColor(col.u, col.v));
		Color directColor = diffuseColor.sum(specularTexColor.mul(specular));
		
		// Indirect Lightning
		Vec3 newRayDir;
		
		Vec3 reflectionDir = col.getRay().getDirection().mul(-1).reflect(col.getNormal());
		newRayDir = sample(scene, exponentTex, reflectionDir);			
		
		Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(Light.EPSILON)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
		QuickCollision qc = scene.collideRay(newRay);
		if (qc != null && !qc.getPrimitive().equals(light )) {
			Collision newCol = qc.completeCollision();
			PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
			
			Color diffuseIndirect = pd.color.mul(diffuse.brdf(newRayDir, col)).mul(diffuse.getColor(col.u, col.v));

			Color specularIndirect = pd.color.mul(brdf(newRayDir, col, exponentTex)).mul(specularTexColor);

			pd.color = directColor.sum(diffuseIndirect.sum(specularIndirect));
			pd.distance += newCol.getPosition().sub(col.getPosition()).len();
			return pd;
		}

		PathData pd = new PathData(directColor);
		return pd;
	}

	public Vec3 sample(Scene scene, double exp, Vec3 direction) {
		MultiJitteredSampler sampler = scene.getSamplerCaches().poll();
		Vec2 sample = sampler.getRandomSample();
		scene.getSamplerCaches().offer(sampler);

		Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, exp).normalize();
		Vec3 newRayDir = MathUtils.tangentToWorldSpace(hemisphereSample, direction);

		return newRayDir;
	}
	
	public double brdf(Vec3 dir, Collision col, double exp) {
		Vec3 r = dir.reflect(col.getNormal());
		return Math.pow(Math.max(0, - r.dot(col.getRay().getDirection())), exp);
	}
}
