package cg.render;

import cg.math.Vec3;
import cg.render.assets.Texture;

public abstract class Material {
	private Texture colorTex;
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

	private Texture getColorTex() {
		return colorTex;
	}


	public Color getSurfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos) {
		Color color = calculateSurfaceColor(col, l, surfaceToLight, camPos);
		if (colorTex != null) {
			Color texColor = colorTex.getSample(col.u, col.v);
//			color = color.mul(texColor);
		}

		return color;
	}

	protected abstract Color calculateSurfaceColor(Collision col, Light l, Vec3 surfaceToLight, Vec3 camPos);
}
