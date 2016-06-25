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

	public Phong(Channel colorChannel, Channel specularColorChannel, Channel exponentChannel) {
		diffuse = new Diffuse(colorChannel);
		
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
		Color diffuseColor = scene.BACKGROUND_COLOR;
		Color specular = scene.BACKGROUND_COLOR;
		
		Color specularTexColor = getSpecularColor(col.u, col.v);
		double exponentTex = getFinalExponent(col.u, col.v);

		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
				Color result = (light.getColor().mul(light.getIntensity())).mul(diffuse.brdf(surfaceToLight, col));
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
		
		Color diffuseColor = scene.BACKGROUND_COLOR;
		Color specular = scene.BACKGROUND_COLOR;
		
		Color specularTexColor = getSpecularColor(col.u, col.v);
		double exponentTex = getFinalExponent(col.u, col.v);

		if (scene.getLights().size() > 0) {
			int index = (int) Math.random() * scene.getAreaLights().size();
			Light light = scene.getLights().get(index);
			Vec3 surfaceToLight = light.vectorFromCollision(col).normalize();
			Color result = (light.getColor().mul(light.getIntensity())).mul(diffuse.brdf(surfaceToLight, col));
			diffuseColor = diffuseColor.sum(result);

			result = (new Color(brdf(surfaceToLight, col, exponentTex)).mul(light.getColor().mul(light.getIntensity())));
			specular = specular.sum(result);
		}
		
		Light light = null;
		if (scene.getAreaLights().size() > 0) {
			int index = (int) Math.random() * scene.getAreaLights().size();
			light = scene.getAreaLights().get(index);
			VisibilityResult visibility = light.sampledVisibleFrom(col);
			if (visibility.isVisible) {
				Vec3 surfaceToLight = visibility.lightSurface.sub(col.getPosition()).normalize();
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
		
		Ray newRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.0001)), newRayDir, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
		QuickCollision qc = scene.collideRay(newRay);
		if (qc != null && !qc.getPrimitive().equals(light )) {
			Collision newCol = qc.completeCollision();
			PathData pd = newCol.getPrimitive().getMaterial().traceSurfaceColor(newCol, scene);
			
			Color diffuseIndirect = pd.color.mul(diffuse.brdf(newRayDir, col)).mul(diffuse.getColor(col.u, col.v));
			diffuseIndirect = diffuseIndirect.mul(2 * Math.PI);
			
			Color specularIndirect = pd.color.mul(brdf(newRayDir, col, exponentTex)).mul(specularTexColor);
			// specularIndirect = specularIndirect.mul(0x4fc3901 * Math.pow(Math.PI, 3));
			
			diffuseIndirect = Color.BLACK;
			pd.color = directColor.sum(diffuseIndirect.sum(specularIndirect));
			pd.distance += newCol.getPosition().sub(col.getPosition()).len();
			return pd;
		}

		PathData pd = new PathData(directColor);
		return pd;
	}

	public Vec3 sample(Scene scene, double exponent, Vec3 direction) {
		MultiJitteredSampler sampler = scene.getSamplerCaches().poll();
		Vec2 sample = sampler.getRandomSample();
		scene.getSamplerCaches().offer(sampler);

		Vec3 hemisphereSample = MathUtils.squareToHemisphere(sample.x, sample.y, exponent).normalize();
		Vec3 newRayDir = MathUtils.tangentToWorldSpace(hemisphereSample, direction);

		return newRayDir;
	}
	
	public double brdf(Vec3 dir, Collision col, double exponent) {
		Vec3 r = dir.reflect(col.getNormal());
		return Math.pow(Math.max(0, - r.dot(col.getRay().getDirection())), exponent);
	}
}
