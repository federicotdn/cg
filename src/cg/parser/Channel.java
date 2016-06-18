package cg.parser;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import cg.math.Vec2;
import cg.render.Color;
import cg.render.assets.Texture;

public class Channel {
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final Double DEFAULT_SCALAR = 1.0;
	
	public enum ChanType {
		COLOR, SPECULAR, REFLECTIVE, REFRACTIVE, EXPONENT, IOR
	}
	
	public final Color colorComponent;
	public final Double scalarComponent;
	public final ChanType type;
	
	public final Integer textureId;
	private Texture texture;
	public final Vec2 textureOffset;
	public final Vec2 textureScale;
	
	public final Warnings warnings;
	
	private Channel(ChanType type, Color colorComponent, Double scalarComponent, Integer textureId, Vec2 textureOffset,
			Vec2 textureScale, Warnings warnings) {
		this.type = type;
		this.colorComponent = colorComponent;
		this.scalarComponent = scalarComponent;
		this.textureId = textureId;
		this.textureOffset = textureOffset;
		this.textureScale = textureScale;
		this.warnings = warnings;
	}
	
	public static Channel getDefaultColorChannel() {
		return getBasicColorChannel(new Color(0.5));
	}

	public static Channel getBasicColorChannel(Color color) {
		Warnings w = new Warnings();
		return new Channel(ChanType.COLOR, color, null, null, null, null, w);
	}
	
	public static Channel getColorChannel(JsonObject o) {
		return coloredChannel(ChanType.COLOR, "color", o);
	}
	
	public static Channel getSpecularChannel(JsonObject o) {
		return coloredChannel(ChanType.SPECULAR, "specularColor", o);
	}
	
	public static Channel getReflectiveChannel(JsonObject o) {
		return coloredChannel(ChanType.REFLECTIVE, "reflectivityColor", o);
	}
	
	public static Channel getRefractiveChannel(JsonObject o) {
		return coloredChannel(ChanType.REFRACTIVE, "refractionColor", o);
	}
	
	public static Channel getExponentChannel(JsonObject o) {
		return scalarChannel(ChanType.EXPONENT, "exponent", o);
	}
	
	public static Channel getIorChannel(JsonObject o) {
		return scalarChannel(ChanType.IOR, "ior", o);
	}
	
	public void setTexture(Texture tex) {
		this.texture = tex;
	}
	
	public Texture getTexture() {
		return texture;
	}

	private static Channel coloredChannel(ChanType t, String chanPrefix, JsonObject o) {
		Color c = parseColor(o, chanPrefix);
		Warnings warnings = new Warnings();
		
		if (c == null) {
			c = DEFAULT_COLOR;
			warnings.add("Channel type: " + t + " using default color.");
		}

		Integer id = parseTextureId(o, chanPrefix);
		Vec2 offset = null, scale = null;
		
		if (id != null) {
			offset = parseTextureOffset(o, chanPrefix);
			scale = parseTextureScale(o, chanPrefix);
			
			if (offset == null) {
				offset = new Vec2(0, 0);
				warnings.add("Channel type: " + t + " using default texture offset.");
			}
			
			if (scale == null) {
				scale = new Vec2(1, 1);
				warnings.add("Channel type: " + t + " using default texture scale.");
			}
		}
		
		return new Channel(t, c, null, id, offset, scale, warnings);
	}
	
	private static Channel scalarChannel(ChanType t, String chanPrefix, JsonObject o) {
		Warnings warnings = new Warnings();
		Double scalar = parseScalar(o, chanPrefix);
		
		if (scalar == null) {
			scalar = DEFAULT_SCALAR;
			warnings.add("Channel type: " + t + " using default scalar value.");
		}

		Integer id = parseTextureId(o, chanPrefix);
		Vec2 offset = null, scale = null;
		
		if (id != null) {
			offset = parseTextureOffset(o, chanPrefix);
			scale = parseTextureScale(o, chanPrefix);
			
			if (offset == null) {
				offset = new Vec2(0, 0);
				warnings.add("Channel type: " + t + " using default texture offset.");
			}
			
			if (scale == null) {
				scale = new Vec2(1, 1);
				warnings.add("Channel type: " + t + " using default texture scale.");
			}
		}
		
		return new Channel(t, null, scalar, id, offset, scale, warnings);		
	}
	
	public boolean isTextured() {
		return textureId != null;
	}
	
	private static Vec2 parseTextureOffset(JsonObject o, String chanPrefix) {
		JsonValue val = o.get(chanPrefix + "Offset");
		if (val == null) {
			return null;
		}
		
		JsonObject data = val.asObject();
		double x = data.getDouble("x", 0);
		double y = data.getDouble("y", 0);
		return new Vec2(x, y);
	}
	
	private static Vec2 parseTextureScale(JsonObject o, String chanPrefix) {
		JsonValue val = o.get(chanPrefix + "Scale");
		if (val == null) {
			return null;
		}
		
		JsonObject data = val.asObject();
		double x = data.getDouble("x", 1);
		double y = data.getDouble("y", 1);
		return new Vec2(x, y);
	}
	
	private static Double parseScalar(JsonObject o, String key) {
		double s = o.getDouble(key, -1);
		if (s < -1) {
			return null;
		}
		
		return s;
	}
	
	private static Integer parseTextureId(JsonObject o, String chanPrefix) {
		int id = o.getInt(chanPrefix + "TextureId", -1);
		return (id == -1 ? null : id);
	}
	
    private static Color parseColor(JsonObject object, String key) {
    	JsonValue val = object.get(key);
    	if (val == null) {
    		return null;
    	}
        JsonObject co = val.asObject();
        return new Color(co.getDouble("a", 1), co.getDouble("r", 1), co.getDouble("g", 1), co.getDouble("b", 1));
    }
}
