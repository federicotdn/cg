package cg.parser;

import cg.math.Vec3;
import cg.render.*;
import cg.render.assets.Mesh;
import cg.render.assets.Texture;
import cg.render.lights.*;
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
	private static final Material DEFAULT_MATERIAL = Diffuse.DEFAULT_DIFFUSE;
	private static final int DEFAULT_SAMPLES = 2;
	
    private String filename;
    private WorldObject rootObject;
    private List<WorldObject> worldObjects;
    private List<Primitive> primitives;
    private Map<Integer, Mesh> meshes;
    private Scene scene;
    private MaterialFactory materialFactory;

    public SceneParser(String filename) {
        this.filename = filename;
        rootObject = new EmptyObject(null, null, null);
        worldObjects = new ArrayList<>();
        primitives = new ArrayList<>();
        meshes = new HashMap<>();
        scene = new Scene();
        materialFactory = new MaterialFactory();
    }

    public Scene parseScene(boolean pathTracingEnabled) {
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
		
		Warnings w = materialFactory.buildMaterials();
		for (String s : w) {
			printWarning(s);
		}
		
		addWorldObjects(object.get("objects").asArray(), rootObject, cameraId);

		JsonObject renderOptions = object.get("renderOptions").asObject();
		scene.setSize(renderOptions.getInt("width", 1920), renderOptions.getInt("height", 1080));
        scene.setReflectionTraceDepth(renderOptions.getInt("reflectionTraceDepth", 2));
        scene.setRefractionTraceDepth(renderOptions.getInt("refractionTraceDepth", 2));
        scene.setBucketSize(renderOptions.getInt("bucketSize", 32));
        scene.setThreads(renderOptions.getInt("threads", 1));

        int samples = renderOptions.getInt("antialiasing", -1);
		if (samples == -1) {
			printWarning("Missing antialiasing sample count.  Defaulting to: " + String.valueOf(DEFAULT_SAMPLES));
			samples = DEFAULT_SAMPLES;
		} else if (samples % 2 == 1) {
			printWarning("Odd antialiasing sample count, increasing by one.");
			samples++;
		}
		
		scene.setSamples((int)Math.pow(2, samples));
		
		rootObject.calculateTransform();

        for (Primitive primitive: primitives) {
            scene.addPrimitive(primitive);
        }

		return scene;
    }
    
    public void addAssets(JsonArray assets) {
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
                    
                    switch (materialType) {
                        case "Diffuse":
                            materialFactory.registerDiffuse(id, o);
                        	break;
                        case "Color":
                        	materialFactory.registerColorMaterial(id, o);
                            break;
                        case "Phong":
                        	materialFactory.registerPhong(id, o);
                        	break;
                        case "Reflective":
                        	materialFactory.registerReflective(id, o);
                            break;
                        case "Refractive":
                        	materialFactory.registerRefractive(id, o);
                            break;
                        case "None":
                        	printWarning("Material of type None found.");
                        	materialFactory.registerAsNone(id);
                        	break;
                        default:
                            printWarning("Unsupported material of type '" + materialType + "'");
                    }

                    break;
                case "Texture":
                    byte[] bytes;
                    
                    try {
                    	bytes = parseBase64(o.getString("base64PNG", ""));
                    } catch (Exception e) {
                		printWarning("Invalid Base64 for Texture data in Asset ID: " + id + ", skipping.");
                		break;
                    }
                    
					Texture texture;
					try {
						texture = new Texture(bytes);
						materialFactory.addTexture(id, texture);
					} catch (Exception e) {
                		printWarning("Invalid Texture data in Asset ID: " + id + ", skipping.");
					}

                    break;
                case "Mesh":
                	byte[] data;
                	try {
                		data = parseBase64(o.getString("base64OBJ", ""));
                	} catch (Exception e) {
                		printWarning("Invalid Base64 for Mesh data in Asset ID: " + id + ", skipping.");
                		break;
                	}
                     
                    Scanner scanner = new Scanner(new ByteArrayInputStream(data)).useLocale(Locale.US);
                    
                    try {
                    	Mesh mesh = scanMesh(scanner);
                    	meshes.put(id, mesh);                    	
                    } catch (Exception e) {
                		printWarning("Invalid Mesh data in Asset ID: " + id + ", skipping.");
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
			printError("File not found.");
			return null;
		}
    	
		JsonObject object;
		try {
			object = Json.parse(reader).asObject();
		} catch (Exception e) {
			printError("Scene file does not contain valid JSON data.");
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

    private Mesh scanMesh(Scanner scanner) {
        List<Double> v = new ArrayList<>();
        List<Double> normals = new ArrayList<>();
        List<Double> uv = new ArrayList<>();
        List<Integer> faces = new ArrayList<>();

        while (scanner.hasNext()) {
            String s = scanner.next();
            switch (s) {
                case "v":
                    v.add(scanner.nextDouble());
                    v.add(scanner.nextDouble());
                    v.add(scanner.nextDouble());
                    break;
                case "vn":
                    normals.add(scanner.nextDouble());
                    normals.add(scanner.nextDouble());
                    normals.add(scanner.nextDouble());
                    break;
                case "vt":
                    uv.add(scanner.nextDouble());
                    uv.add(scanner.nextDouble());
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

        return new Mesh(v, normals, uv, faces);
    }
    
    private void addWorldObjects(JsonArray objects, WorldObject parent, int cameraId) {
        for (JsonValue value: objects) {
            JsonObject o = value.asObject();
            String type = o.getString("type", "");

            int id = o.getInt("id", -1);
            if (id == -1) {
                printWarning("Object of type '" + "' doesn't have an ID, skipping.");
                continue;
            }

            WorldObject wo;
            switch (type) {
                case "Camera":
                    if (id == cameraId) {
                        Camera cam = new Camera(getPosition(o),
                                getRotation(o),
                                o.getDouble("fieldOfView", 60));
                        scene.setCam(cam);
                        wo = cam;
                    } else {
                    	wo = null;
                    	printWarning("Ignored camera with ID: " + String.valueOf(id));
                    }
                    break;
                case "Light":
                    String lightType = o.getString("lightType", "");
                    Light light;

                    double intensity =  o.getDouble("intensity", 0);
                    Color color = parseColor(o, "color");

                    switch (lightType) {
                        case "Directional":
                            light = new DirectionalLight(scene, color, intensity, getRotation(o));
                        break;
                        case "Spot":
                            light = new SpotLight(scene, color, intensity, getPosition(o),
                                    getRotation(o), o.getDouble("spotAngle", 60));
                            break;
                        case "Point":
                            light = new PointLight(scene, color, intensity, getPosition(o));
                            break;
                        case "Ambient":
                        	light = new AmbientLight(scene, color, intensity);
                        	break;
                        case "Rectangle":
                            light = new RectangleAreaLight(scene, color, intensity, getPosition(o), getRotation(o),
                                    getScale(o), o.getDouble("width", 1), o.getDouble("height", 1));
                            break;
                        case "Sphere":
                            light = new SphereAreaLight(scene, color, intensity, getPosition(o), getRotation(o),
                                    getScale(o), o.getDouble("radius", 1));
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
                            Sphere sphere = new Sphere(getPosition(o), getRotation(o), getScale(o), o.getDouble("radius", 1));
                            primitive = sphere;
                        break;

                        case "Plane":
                        	boolean finite = o.getBoolean("finite", false);
                        	
                        	if (finite) {
                            	double width = o.getDouble("width", 1);
                            	double depth = o.getDouble("height", 1);
                            	primitive = new FinitePlane(width, depth, getPosition(o), getRotation(o), getScale(o));
                        	} else {
                        		primitive = new InfinitePlane(getPosition(o), getRotation(o), getScale(o));
                        	}
                        break;
                        
                        case "Cube":
                        	double width = o.getDouble("width", 1);
                        	double depth = o.getDouble("depth", 1);
                        	double height = o.getDouble("height", 1);
                        	
                        	primitive = new Box(width, height, depth, getPosition(o), getRotation(o), getScale(o));
                        	break;
                        	
                        case "Mesh":
                            int meshId = o.getInt("meshId", -1);
                            if (!meshes.containsKey(meshId)) {
                                printWarning("WorldObject ID: " + id + " Referenced mesh ID: " + meshId +  " does not exist, skipping object.");
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
                    	
                    	if (materialId == -1 || !materialFactory.containsMaterial(materialId)) {
                    		printWarning("Shape ID: " + String.valueOf(id) + " does not specify a valid material, using default.");
                    		primitive.setMaterial(DEFAULT_MATERIAL);
                    	} else {
                    		primitive.setMaterial(materialFactory.getMaterial(materialId));
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
        return new Vec3(object.getDouble("x", defaultVal), object.getDouble("y", defaultVal), object.getDouble("z", defaultVal));

    }

    private Color parseColor(JsonObject object, String key) {
        JsonObject co = object.get(key).asObject();
        return new Color(co.getDouble("a", 1), co.getDouble("r", 1), co.getDouble("g", 1), co.getDouble("b", 1));
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
