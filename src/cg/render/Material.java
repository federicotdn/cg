package cg.render;

public abstract class Material {	
//	protected Color getTextureColorMix(double u, double v) {
//		return getTextureColorMix(u, v, colorTex);
//	}
//
//	protected Color getTextureColorMix(double u, double v, Texture tex) {
//		if (tex == null) {
//			return color;
//		}
//
//		return tex.getSample(u, v).mul(color);
//	}
	public abstract Color getSurfaceColor(Collision col, Scene scene);
}
