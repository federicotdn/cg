package cg.render.materials;

import cg.math.Vec2;
import cg.parser.Channel;
import cg.render.Collision;
import cg.render.Color;
import cg.render.Material;
import cg.render.PathData;
import cg.render.Scene;
import cg.render.assets.Texture;

public class ColorMaterial extends Material {
	//New fields:
	private final Color color;
	private final Texture colorTexture;
	private final Vec2 colorTextureOffset;
	private final Vec2 colorTextureScale;
	
	public ColorMaterial(Channel colorChannel) {
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
	public boolean isEmissive() {
		return true;
	}

	@Override
	public Color getSurfaceColor(Collision col, Scene scene) {
		Color myColor = color;
		
		if (colorTexture != null) {
			Color texCol = colorTexture.getOffsetScaledSample(colorTextureOffset, colorTextureScale, col.u, col.v);
			myColor = myColor.mul(texCol);
		}
		return myColor;
	}

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
		// Direct
		
		PathData pd = new PathData(getSurfaceColor(col, scene));
		return pd;
	}
}
