package cg.parser;

import cg.render.Color;
import cg.render.assets.Texture;

public class DefaultTexture extends Texture {
	private static final Color DEFAULT_COLOR = new Color(1, 0, 0.5);
	
	private static DefaultTexture instance = null;
	
	public static DefaultTexture getInstance() {
		if (instance == null) {
			instance = new DefaultTexture();
		}
		
		return instance;
	}
	
	private DefaultTexture() {
		
	}
	
	@Override
	public Color getSample(double u, double v) {
		return DEFAULT_COLOR;
	}
}
