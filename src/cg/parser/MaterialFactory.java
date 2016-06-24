package cg.parser;

import cg.parser.Channel.ChanType;
import cg.render.Material;
import cg.render.assets.Texture;
import cg.render.materials.*;
import com.eclipsesource.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MaterialFactory {
	private static final Material DEFAULT_MATERIAL = Diffuse.DEFAULT_DIFFUSE;
	
	private enum MaterialType {
		COLOR, DIFFUSE, PHONG, REFLECTIVE, REFRACTIVE
	}
	
	private class MaterialData {
		public Map<ChanType, Channel> channels;
		public MaterialType type;
		
		public MaterialData(MaterialType type) {
			this.channels = new HashMap<>();
			this.type = type;
		}
		
		public void addChan(Channel ch) {
			channels.put(ch.type, ch);
		}
	}
	
	private Texture defaultTexture;
	
	private Map<Integer, Texture> textures;
	private Map<Integer, MaterialData> materialData;
	private Map<Integer, Material> materials;
	
	public MaterialFactory() {
		defaultTexture = DefaultTexture.getInstance();
		textures = new HashMap<>();
		materialData = new HashMap<>();
		materials = new HashMap<>();
	}
	
	public void addTexture(Integer id, Texture tex) {
		textures.put(id, tex);
	}
	
	public boolean hasTexture(Integer id) {
		return textures.containsKey(id);
	}
	
	public void registerAsNone(Integer id) {
		materials.put(id, DEFAULT_MATERIAL);
	}
	
	public void registerDiffuse(Integer id, JsonObject o) {
		Channel colorChan = Channel.getColorChannel(o);
		
		MaterialData md = new MaterialData(MaterialType.DIFFUSE);
		md.addChan(colorChan);
		
		materialData.put(id, md);
	}
	
	public void registerColorMaterial(Integer id, JsonObject o) {
		Channel colorChan = Channel.getColorChannel(o);
		
		MaterialData md = new MaterialData(MaterialType.COLOR);
		md.addChan(colorChan);
		
		materialData.put(id, md);
	}

	public Texture getTexture(int id) {
		if (textures.containsKey(id)) {
			return textures.get(id);
		}

		return defaultTexture;
	}
	
	public void registerPhong(Integer id, JsonObject o) {
		Channel colorChan = Channel.getColorChannel(o);
		Channel specularChan = Channel.getSpecularChannel(o);
		Channel exponentChan = Channel.getExponentChannel(o);
		
		MaterialData md = new MaterialData(MaterialType.PHONG);
		md.addChan(colorChan);
		md.addChan(specularChan);
		md.addChan(exponentChan);
		
		materialData.put(id, md);
	}
	
	public void registerReflective(Integer id, JsonObject o) {
		Channel reflectiveChan = Channel.getReflectiveChannel(o);
		
		MaterialData md = new MaterialData(MaterialType.REFLECTIVE);
		md.addChan(reflectiveChan);
		
		materialData.put(id, md);	
	}
	
	public void registerRefractive(Integer id, JsonObject o) {
		Channel reflectiveChan = Channel.getReflectiveChannel(o);
		Channel refractiveChan = Channel.getRefractiveChannel(o);
		Channel iorChannel = Channel.getIorChannel(o);
		
		MaterialData md = new MaterialData(MaterialType.REFRACTIVE);
		md.addChan(reflectiveChan);
		md.addChan(iorChannel);
		md.addChan(refractiveChan);
		
		materialData.put(id, md);
	}
	
	public Material getMaterial(Integer id) {
		return materials.get(id);
	}
	
	public boolean containsMaterial(Integer id) {
		return materials.containsKey(id);
	}
	
	public Warnings buildMaterials() {
		Warnings warnings = new Warnings();
		warnings.setHeader("Build Materials:");
		
		for (Entry<Integer, MaterialData> entry : materialData.entrySet()) {
			MaterialData md = entry.getValue();
			Integer id = entry.getKey();

			Warnings matWarnings = new Warnings();
			matWarnings.setHeader("Material ID: " + id);			

			for (Channel ch : md.channels.values()) {
				matWarnings.copyFrom(ch.warnings);
				
				if (ch.isTextured()) {
					Texture tex = textures.get(ch.textureId);
					if (tex == null) {
						matWarnings.add("Channel type: " + ch.type + " references invalid texture ID: " + ch.textureId + ", using default.");
						tex = defaultTexture;
					}
					
					ch.setTexture(tex);
				}
			}
			
			Material mat;
			switch (md.type) {
			case COLOR:
				mat = new ColorMaterial(md.channels.get(ChanType.COLOR));
				break;
			case DIFFUSE:
				mat = new Diffuse(md.channels.get(ChanType.COLOR));
				break;
			case PHONG:
				mat = new Phong(md.channels.get(ChanType.COLOR), md.channels.get(ChanType.SPECULAR), md.channels.get(ChanType.EXPONENT));
				break;
			case REFLECTIVE:
				mat = new ReflectiveMaterial(md.channels.get(ChanType.REFLECTIVE));
				break;
			case REFRACTIVE:
				mat = new RefractiveMaterial(md.channels.get(ChanType.REFRACTIVE), md.channels.get(ChanType.REFLECTIVE), md.channels.get(ChanType.IOR));
				break;
			default:
				matWarnings.add("Invalid material type, skipping.");
				mat = null;
				break;
			}
			
			if (mat != null) {				
				materials.put(id, mat);
			}
			
			warnings.copyFrom(matWarnings);
		}
		
		return warnings;
	}
}
