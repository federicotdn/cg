package cg.parser;

import cg.math.Vec3;
import cg.render.*;
import cg.render.lights.AmbientLight;
import cg.render.lights.DirectionalLight;
import cg.render.lights.PointLight;
import cg.render.lights.SpotLight;
import cg.render.materials.ColorMaterial;
import cg.render.materials.Diffuse;
import cg.render.shapes.Box;
import cg.render.shapes.FinitePlane;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.Sphere;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by Hobbit on 4/22/16.
 */
public class SceneParser {
	private static final Material DEFAULT_MATERIAL = Diffuse.DIFFUSE_DEFAULT;
	private static final float HIGH_INTENSITY = 0.6f;
	
    private String filename;
    private WorldObject rootObject;
    private List<WorldObject> worldObjects;
    private List<Primitive> primitives;
    private Map<Integer, Material> materials;
    private Scene scene;

    public SceneParser(String filename) {
        this.filename = filename;
        rootObject = new EmptyObject(null, null, null);
        worldObjects = new ArrayList<>();
        primitives = new ArrayList<>();
        materials = new HashMap<>();
        scene = new Scene();
    }

    public Scene parseScene() {
    	JsonObject object = readJSONFile();
    	if (object == null) {
    		return null;
    	}
    	
		int cameraId = object.get("mainCameraId").asInt();
		if (cameraId == -1) {
			printError("Invalid Scene: no main camera set.");
			return null;
		}
		
		addAssets(object.get("assets").asArray());
		addWorldObjects(object.get("objects").asArray(), rootObject, cameraId);

		JsonObject renderOptions = object.get("renderOptions").asObject();
		scene.setSize(renderOptions.getInt("width", 1920), renderOptions.getInt("height", 1080));

		for (WorldObject wo : worldObjects) {
			wo.calculateTransform();
		}

		return scene;
    }
    
    public void addAssets(JsonArray assets) {
    	for (JsonValue value : assets) {
			JsonObject o = value.asObject();

			String assetType = o.getString("assetType", "");
			switch (assetType) {
			case "Material":
				String materialType = o.getString("materialType", "");
				int colorTextureId = o.getInt("colorTextureId", -1);
				
				Material material;

				switch (materialType) {
				case "Diffuse":
					material = new Diffuse(parseColor(o, "color"));
					break;
				case "Color":
					Color c = parseColor(o, "color");
					material = new ColorMaterial(c);
					break;
				default:
					material = null;
					printWarning("Unsupported material of type '" + materialType + "'");
				}

				if (material != null) {
					int id = o.getInt("id", -1);
					if (id != -1) {
						if (colorTextureId != -1) {
							//TODO: Handle adding Textures to materials
							//Print a warning for now
							printWarning("Material ID: " + String.valueOf(id) + " references a Texture (TODO).");
						}
						
						materials.put(id, material);
					} else {
						printWarning("Found material with invalid ID.");
					}
				}
				break;
			default:
				printWarning("Unsupported asset of type '" + assetType + "'");
			}

		}
    }
    
    private JsonObject readJSONFile() {
    	Reader reader;
		try {
			reader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			printError("Unable to read scene file.");
			return null;
		}
    	
		JsonObject object;
		try {
			object = Json.parse(reader).asObject();
		} catch (IOException e) {
			printError("Scene file does contain valid JSON data.");
			try {
				reader.close();
			} catch (IOException e1) {
				/* EMPTY */
			}
			return null;
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			return null;
		}
		
		return object;
    }

    private void addWorldObjects(JsonArray objects, WorldObject parent, int cameraId) {
        for (JsonValue value: objects) {
            JsonObject o = value.asObject();
            String type = o.getString("type", "");

            int id = o.getInt("id", -1);
            if (id == -1) {
                printWarning("Object of type '" + "' doesn't have an id");
                continue;
            }

            WorldObject wo;
            switch (type) {
                case "Camera":
                	wo = null;
                    if (id == cameraId) {
                        Camera cam = new Camera(getPosition(o),
                                getRotation(o),
                                o.getFloat("fieldOfView", 60));
                        scene.setCam(cam);
                    } else {
                    	printWarning("Ignored camera with ID: " + String.valueOf(id));
                    }
                    break;
                case "Light":
                    String lightType = o.getString("lightType", "");
                    Light light;

                    float intensity =  o.getFloat("intensity", 0);
                    if (intensity > HIGH_INTENSITY) {
                    	printWarning("Light ID: " + String.valueOf(id) + " has very high intensity.");
                    }

                    Color color = parseColor(o, "color");

                    switch (lightType) {
                        case "Directional":
                            light = new DirectionalLight(scene, color, intensity, getRotation(o));
                        break;
                        case "Spot":
                            light = new SpotLight(scene, color, intensity, getPosition(o),
                                    getRotation(o), o.getFloat("spotAngle", 60));
                            break;
                        case "Point":
                            light = new PointLight(scene, color, intensity, getPosition(o));
                            break;
                        case "Ambient":
                        	light = new AmbientLight(scene, color, intensity);
                        	break;
                            
                        default:
                        	light = null;
                            printWarning("Unsupported light of type '" + lightType + "' ");
                            break;
                    }

                    wo = light;
                    if (light != null) {
                        scene.addLight(light);
                        worldObjects.add(light);
                    } 
                    break;
                case "Shape":
                    String shapeType = o.get("shapeType").asString();
                    String name = o.getString("name", "undefined name");
                    int materialId = o.getInt("materialID", -1);
                    
                    Primitive primitive;
                    
                    switch (shapeType) {
                        case "Sphere":
                            Sphere sphere = new Sphere(getPosition(o), getRotation(o), getScale(o), o.getFloat("radius", 1));
                            primitive = sphere;
                        break;

                        case "Plane":
                        	boolean finite = o.getBoolean("finite", false);
                        	
                        	if (finite) {
                            	float width = o.getFloat("width", 1);
                            	float depth = o.getFloat("height", 1);
                            	primitive = new FinitePlane(width, depth, getPosition(o), getRotation(o), getScale(o));
                        	} else {
                        		primitive = new InfinitePlane(getPosition(o), getRotation(o), getScale(o));
                        	}
                        break;
                        
                        case "Cube":
                        	float width = o.getFloat("width", 1);
                        	float depth = o.getFloat("depth", 1);
                        	float height = o.getFloat("height", 1);
                        	
                        	primitive = new Box(width, height, depth, getPosition(o), getRotation(o), getScale(o));
                        	break;
                        	
                        case "Mesh":
                        	primitive = null;
                        	printWarning("Not implemented: Mesh");
                        	break;

					default:
						primitive = null;
                        printWarning("Unsupported shape of type '" + shapeType + "'");
                    }
                    
                    wo = primitive;
                    if (primitive != null) {
                    	primitive.setName(name);
                    	scene.addPrimitive(primitive);
                    	primitives.add(primitive);
                    	
                    	if (materialId == -1 || !materials.containsKey(materialId)) {
                    		printWarning("Shape ID: " + String.valueOf(id) + " does not specify a valid material, using default.");
                    		primitive.setMaterial(DEFAULT_MATERIAL);
                    	} else {
                    		primitive.setMaterial(materials.get(materialId));
                    	}
                    }
                    
                    break; //case "Shape"

                case "None":
                    EmptyObject eo = new EmptyObject(getPosition(o), getRotation(o), getScale(o));
                    wo = eo;
                    break;
                default:
                	wo = null;
                    printWarning("Unsupported object of type '" + type + "'");
            }

            if (parent != null && wo != null) {
                parent.addChild(wo);
            }

            if (wo != null) {
                worldObjects.add(wo);
                JsonArray childrenArray = o.get("children").asArray();
                if (childrenArray.size() != 0) {
                    addWorldObjects(childrenArray, wo, cameraId);
                }
            }
        }
    }
    
    private byte[] parseBase64(String s) {
    	return Base64.getDecoder().decode(s.getBytes());
    }

    private Vec3 valueToVec3(JsonValue jsonValue, int defaultVal) {
        JsonObject object = jsonValue.asObject();
        return new Vec3(object.getFloat("x", defaultVal), object.getFloat("y", defaultVal), object.getFloat("z", defaultVal));

    }

    private Color parseColor(JsonObject object, String key) {
        JsonObject co = object.get(key).asObject();
        return new Color(co.getFloat("a", 0), co.getFloat("r", 0), co.getFloat("g", 0), co.getFloat("b", 0));
    }

    private Vec3 getPosition(JsonObject object) {
        return valueToVec3(object.get("localPosition"), 0);
    }

    private Vec3 getRotation(JsonObject object) {
        return valueToVec3(object.get("localRotation"), 0);
    }

    private Vec3 getScale(JsonObject object) {
        return valueToVec3(object.get("localScale"), 1);
    }

    private void printWarning(String s) {
        System.out.println("WARNING: " + s);
    }
    
    private void printError(String s) {
        System.out.println("ERROR: " + s);
    }
}
