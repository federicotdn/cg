package cg.parser;

import cg.math.Vec3;
import cg.render.*;
import cg.render.assets.Mesh;
import cg.render.assets.Texture;
import cg.render.lights.AmbientLight;
import cg.render.lights.DirectionalLight;
import cg.render.lights.PointLight;
import cg.render.lights.SpotLight;
import cg.render.materials.*;
import cg.render.shapes.*;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.ByteArrayInputStream;
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
	private static final int DEFAULT_SAMPLES = 2;
	private static final int DEFAULT_EXPONENT = 2;
	
    private String filename;
    private WorldObject rootObject;
    private List<WorldObject> worldObjects;
    private List<Primitive> primitives;
    private Map<Integer, Material> materials;
    private Map<Integer, Mesh> meshes;
    private Scene scene;

    public SceneParser(String filename) {
        this.filename = filename;
        rootObject = new EmptyObject(null, null, null);
        worldObjects = new ArrayList<>();
        primitives = new ArrayList<>();
        materials = new HashMap<>();
        meshes = new HashMap<>();
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
        scene.setReflectionTraceDepth(renderOptions.getInt("reflectionTraceDepth", 2));
        scene.setRefractionTraceDepth(renderOptions.getInt("refractionTraceDepth", 2));

        int samples = renderOptions.getInt("antialiasing", -1);
		if (samples == -1) {
			printWarning("Missing antialiasing sample count.  Defaulting to: " + String.valueOf(DEFAULT_SAMPLES));
			samples = DEFAULT_SAMPLES;
		} else if (samples % 2 == 1) {
			printWarning("Odd antialiasing sample count, increasing by one.");
			samples++;
		}
		
		scene.setSamples((int)Math.pow(2, samples));
		
		for (WorldObject wo : worldObjects) {
			wo.calculateTransform();
		}

        for (Primitive primitive: primitives) {
            scene.addPrimitive(primitive);
        }

		return scene;
    }
    
    public void addAssets(JsonArray assets) {
        Map<Integer, Texture> textures = new HashMap<>();
        Map<Integer, List<Material>> requiredTextures = new HashMap<>();

    	for (JsonValue value : assets) {
			JsonObject o = value.asObject();

            String assetType = o.getString("assetType", "");
            int id = o.getInt("id", -1);
            if (id == -1) {
                printWarning("Asset of type '" + assetType + "' does not have an id. Skipping.");
                continue;
            }

            switch (assetType) {
                case "Material":
                    String materialType = o.getString("materialType", "");
                    int colorTextureId = o.getInt("colorTextureId", -1);

                    float offsetU = 0 , offsetV = 0, scaleU = 1, scaleV = 1;
                    if (colorTextureId != -1) {
                        JsonObject colorOffset = o.get("colorOffset").asObject();
                        offsetU = colorOffset.getFloat("x", 0);
                        offsetV = colorOffset.getFloat("y", 0);

                        JsonObject scaleOffset = o.get("colorScale").asObject();
                        scaleU = scaleOffset.getFloat("x", 0);
                        scaleV = scaleOffset.getFloat("y", 0);
                    }

                    Material material;
                    switch (materialType) {
                        case "Diffuse":
                            material = new Diffuse(parseColor(o, "color"), offsetU, offsetV, scaleU, scaleV);
                            break;
                        case "Color":
                            Color c = parseColor(o, "color");
                            material = new ColorMaterial(c, offsetU, offsetV, scaleU, scaleV);
                            break;
                        case "Phong":
                        	c = parseColor(o, "color");
                        	Color specular = parseColor(o, "specularColor");
                        	float exponent = o.getFloat("exponent", -1);
                        	if (exponent < 0) {
                        		printWarning("Phong material ID " + String.valueOf(id) + " does not have an exponent, using default value.");
                        		exponent = DEFAULT_EXPONENT;
                        	}
                        	
                        	Phong p = new Phong(c, offsetU, offsetV, scaleU, scaleV, specular, exponent);
                        	material = p;
                        	break;
                        case "Reflective":
                            material = new ReflectiveMaterial(parseColor(o, "reflectivityColor"), offsetU, offsetV, scaleU, scaleV);
                            break;
                        case "Refractive":
                            material = new RefractiveMaterial(parseColor(o, "reflectivityColor"), offsetU, offsetV,
                                    scaleU, scaleV, parseColor(o, "refractionColor"), o.getFloat("ior", 1));
                            break;
                        default:
                            material = null;
                            printWarning("Unsupported material of type '" + materialType + "'");
                    }

                    if (material != null) {
                        if (colorTextureId != -1) {
                            if (textures.containsKey(colorTextureId)) {
                                material.setColorTex(textures.get(colorTextureId));
                            } else {
                                if (!requiredTextures.containsKey(colorTextureId)) {
                                    requiredTextures.put(colorTextureId, new ArrayList<>());
                                }
                                requiredTextures.get(colorTextureId).add(material);
                            }
                        } else {
                            printWarning("Material of id " + id + " does not has a texture");
                        }

                        materials.put(id, material);
                    }
                    break;
                case "Texture":
                    byte[] bytes = parseBase64(o.getString("base64PNG", ""));
                    Texture texture = new Texture(bytes);
                    textures.put(id, texture);
                    if (requiredTextures.containsKey(id)) {
                        List<Material> materials = requiredTextures.remove(id);
                        for (Material mat : materials) {
                            mat.setColorTex(texture);
                        }
                    }

                    break;
                case "Mesh":
                    byte[] data = parseBase64(o.getString("base64OBJ", ""));
                    Scanner scanner = new Scanner(new ByteArrayInputStream(data));
                    List<Float> v = new ArrayList<>();
                    List<Float> normals = new ArrayList<>();
                    List<Float> uv = new ArrayList<>();
                    List<Integer> faces = new ArrayList<>();
                    while (scanner.hasNext()) {
                        String s = scanner.next();
                        switch (s) {
                            case "v":
                                v.add(scanner.nextFloat());
                                v.add(scanner.nextFloat());
                                v.add(scanner.nextFloat());
                                break;
                            case "vn":
                                normals.add(scanner.nextFloat());
                                normals.add(scanner.nextFloat());
                                normals.add(scanner.nextFloat());
                                break;
                            case "vt":
                                uv.add(scanner.nextFloat());
                                uv.add(scanner.nextFloat());
                                break;
                            case "f":
                                String[] f = scanner.next().split("/");
                                faces.add(Integer.parseInt(f[0]) - 1);
                                faces.add(Integer.parseInt(f[1]) - 1);
                                faces.add(Integer.parseInt(f[2]) - 1);

                                f = scanner.next().split("/");
                                faces.add(Integer.parseInt(f[0]) - 1);
                                faces.add(Integer.parseInt(f[1]) - 1);
                                faces.add(Integer.parseInt(f[2]) - 1);

                                f = scanner.next().split("/");
                                faces.add(Integer.parseInt(f[0]) - 1);
                                faces.add(Integer.parseInt(f[1]) - 1);
                                faces.add(Integer.parseInt(f[2]) - 1);
                                break;
                            default:
                                printWarning("Invalid character found in mesh '" + s + "'");
                        }
                    }

                    Mesh mesh = new Mesh(v, normals, uv, faces);
                    meshes.put(id, mesh);

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
                            int meshId = o.getInt("meshId", -1);
                            if (!meshes.containsKey(meshId)) {
                                printWarning("Referenced mesh id " + meshId +  " does not exist");
                                primitive = null;
                            } else {
                                primitive = new MeshInstance(meshes.get(meshId), getPosition(o), getRotation(o), getScale(o));
                            }
                        	break;

					default:
						primitive = null;
                        printWarning("Unsupported shape of type '" + shapeType + "'");
                    }
                    
                    wo = primitive;
                    if (primitive != null) {
                    	primitive.setName(name);
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
        return new Color(co.getFloat("a", 1), co.getFloat("r", 1), co.getFloat("g", 1), co.getFloat("b", 1));
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
        System.err.println("ERROR: " + s);
    }
}
