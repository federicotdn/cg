package cg.render;

import cg.render.assets.Texture;

public abstract class Material {
	protected Texture colorTex;
	protected final Color color;
	private double offsetU;
	private double offsetV;
	private double scaleU;
	private double scaleV;

	public Material(Color color, double offsetU, double offsetV, double scaleU, double scaleV) {
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

	public double getOffsetU() {
		return offsetU;
	}

	public double getOffsetV() {
		return offsetV;
	}

	public double getScaleU() {
		return scaleU;
	}

	public double getScaleV() {
		return scaleV;
	}

	public void setColorTex(Texture tex) {
		colorTex = tex;
	}
	
	protected Color getTextureColorMix(double u, double v) {
		return getTextureColorMix(u, v, colorTex);
	}

	protected Color getTextureColorMix(double u, double v, Texture tex) {
		if (tex == null) {
			return color;
		}

		return tex.getSample(u, v).mul(color);
	}

	public abstract Color getSurfaceColor(Collision col, Scene scene);
}
