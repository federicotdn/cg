package cg.render;

import cg.render.assets.Texture;

public abstract class Material {
	protected Texture colorTex;
	protected final Color color;
	private float offsetU;
	private float offsetV;
	private float scaleU;
	private float scaleV;

	public Material(Color color, float offsetU, float offsetV, float scaleU, float scaleV) {
		this.offsetU = offsetU;
		this.offsetV = offsetV;
		this.scaleU = scaleU;
		this.scaleV = scaleV;
		this.color = color;
		this.colorTex = null;
	}

	public Color getColor() {
		return color;
	}

	public float getOffsetU() {
		return offsetU;
	}

	public float getOffsetV() {
		return offsetV;
	}

	public float getScaleU() {
		return scaleU;
	}

	public float getScaleV() {
		return scaleV;
	}

	public void setColorTex(Texture tex) {
		colorTex = tex;
	}
	
	protected Color getTextureColorMix(float u, float v) {
		return getTextureColorMix(u, v, colorTex);
	}

	protected Color getTextureColorMix(float u, float v, Texture tex) {
		if (tex == null) {
			return Color.WHITE;
		}

		return tex.getSample(u, v).mul(color);
	}

	public abstract Color getSurfaceColor(Collision col, Scene scene);
}
