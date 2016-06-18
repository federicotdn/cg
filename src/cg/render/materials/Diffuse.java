package cg.render.materials;

import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Light;
import cg.render.Material;
import cg.render.PathData;
import cg.render.Scene;
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
		Color c = new Color(1, 0, 0, 0);

		for (Light light : scene.getLights()) {
			if (light.visibleFrom(col)) {
				Color result = (light.getColor().mul(light.getIntensity()));
				c = c.sum(result);
			}
		}

		Color myColor = color;
		if (colorTexture != null) {
			Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, col.u, col.v);
			myColor = myColor.mul(texCol);
		}
		
		// Indirect Lightning
		PathData pd = new PathData();
		pd.color = myColor.mul(c);
		return pd;
	}
}
