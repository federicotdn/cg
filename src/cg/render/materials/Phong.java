package cg.render.materials;

import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.*;
import cg.render.assets.Texture;

public class Phong extends Material {	
	//New fields:
	
	//color channel
	private final Color color;
	private final Texture colorTexture;
	private final Vec2 colorTextureOffset;
	private final Vec2 colorTextureScale;
	
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
		Color diffuse = scene.BACKGROUND_COLOR;
		Color specular = scene.BACKGROUND_COLOR;

		Color diffuseTexColor = color;
		if (colorTexture != null) {
			Color texColor = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, col.u, col.v);
			diffuseTexColor = diffuseTexColor.mul(texColor);
		}
		
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
				double cosAngle = surfaceToLight.dot(col.getNormal());
				Color result = (light.getColor().mul(light.getIntensity())).mul(cosAngle);
				diffuse = diffuse.sum(result);

				Vec3 r = surfaceToLight.reflect(col.getNormal());
				double spec = Math.pow(Math.max(0, - r.dot(col.getRay().getDirection())), exponentTex);
				result = (new Color(spec).mul(light.getColor().mul(light.getIntensity())));
				specular = specular.sum(result);
			}
		}

		return diffuse.mul(diffuseTexColor).sum(specularTexColor.mul(specular));
	}
}
