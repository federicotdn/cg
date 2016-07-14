package cg.render;

import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.assets.Texture;

public abstract class Material {
	private final Texture normalTexture;
	private final Vec2 normalTextureOffset;
	private final Vec2 normalTextureScale;

	public Material(Channel normalChannel ) {
		if (normalChannel != null && normalChannel.isTextured()) {
			this.normalTexture = normalChannel.getTexture();
			this.normalTextureOffset = normalChannel.textureOffset;
			this.normalTextureScale = normalChannel.textureScale;
		} else {
			this.normalTexture = null;
			this.normalTextureOffset = null;
			this.normalTextureScale = null;
		}
	}

	public Material() {
		this(null);
	}

	public Vec3 getNormal(double u, double v) {
		Color c = normalTexture.getOffsetScaledSample(normalTextureOffset, normalTextureScale, u, v);
		Vec3 normal = new Vec3(c.getRed(), c.getGreen(), c.getBlue());
		normal = normal.mul(2).sub(new Vec3(1,1,1));
		return normal;
	}

	public boolean hasNormalMap() {
		return normalTexture != null;
	}

	protected Channel normalMap;

	public abstract Color getSurfaceColor(Collision col, Scene scene);
	public abstract PathData traceSurfaceColor(Collision col, Scene scene);
}
