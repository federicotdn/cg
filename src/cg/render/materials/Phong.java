package cg.render.materials;

import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.*;
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
	
	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color diffuseColor = scene.BACKGROUND_COLOR;
		Color specular = scene.BACKGROUND_COLOR;
		
		Color specularTexColor = specularColor;
		if (specularColorTexture != null) {
			Color texColor = specularColorTexture.getOffsetScaledSample(specularColorTextureOffset, specularColorTextureScale, col.u, col.v);
			specularTexColor = specularTexColor.mul(texColor);
		}
		
		double exponentTex = exponent;
		if (exponentTexture != null) {
			Color texColor = exponentTexture.getOffsetScaledSample(exponentTextureOffset, exponentTextureScale, col.u, col.v);
			exponentTex *= texColor.getRed(); // Assuming image is grayscale
		}

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
		// TODO Auto-generated method stub
		return null;
	}

	public double brdf(Vec3 dir, Collision col, double exponent) {
		Vec3 r = dir.reflect(col.getNormal());
		return Math.pow(Math.max(0, - r.dot(col.getRay().getDirection())), exponent);
	}
}
