package cg.parser;

import cg.math.Vec3;
import cg.render.*;
import cg.render.lights.DirectionalLight;
import cg.render.lights.PointLight;
import cg.render.lights.SpotLight;
import cg.render.materials.Lambert;
import cg.render.shapes.Sphere;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hobbit on 4/22/16.
 */
public class SceneParser {
    private String filename;

    public SceneParser(String filename) {
        this.filename = filename;
    }

    public Scene parseScene() {
        try {
            Reader reader = new FileReader(filename);
            JsonObject object = Json.parse(reader).asObject();

            int cameraId = object.get("mainCameraId").asInt();

            List<WorldObject> worldObjects = new ArrayList<>();
            List<Primitive> primitives = new ArrayList<>();
            Map<Integer, List<Primitive>> requiredMaterials = new HashMap<>();
            List<Light> lights = new ArrayList<>();
            EmptyObject rootObj = new EmptyObject(null, null, null);

            Scene scene = new Scene();
            addWorldObjects(object.get("objects").asArray(), worldObjects, requiredMaterials, rootObj, scene, cameraId);

            for (JsonValue value: object.get("assets").asArray()) {
                JsonObject o = value.asObject();

                String assetType = o.getString("assetType", "");
                switch (assetType) {
                    case "Material":
                        String materialType = o.getString("materialType", "");
                        Material material = null;
                        switch (materialType) {
                            case "Diffuse":
                                material = new Lambert(parseColor(o, "color"));
                                break;
                            default:
                                printWarning("Unsupported material of type '" + materialType + "'");

                        }

                        if (material != null) {
                            int id = o.getInt("id", 0);
                            if (requiredMaterials.containsKey(id)) {
                                for (Primitive primitive : requiredMaterials.get(id)) {
                                    primitive.setMaterial(material);
                                }
                            }
                        }
                        break;
                    default:
                        printWarning("Unsupported asset of type '" + assetType +  "'");
                }

            }

            JsonObject renderOptions = object.get("renderOptions").asObject();

            scene.setSize( renderOptions.getInt("width", 1920), renderOptions.getInt("height", 1080));

            for (WorldObject wo: worldObjects) {
                wo.calculateTransform();
            }

            reader.close();
            return scene;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private void addWorldObjects(JsonArray objects, List<WorldObject> worldObjects,
                                 Map<Integer, List<Primitive>> requiredMaterial,
                                 WorldObject parent, Scene scene, int cameraId) {
        for (JsonValue value: objects) {
            JsonObject o = value.asObject();
            String type = o.getString("type", "");

            int id = o.getInt("id", -1);
            if (id == -1) {
                printWarning("Object of type '" + "' doesn't have an id");
                continue;
            }
            WorldObject wo = null;
            switch (type) {
                case "Camera":
                    if (id == cameraId) {
                        Camera cam = new Camera(getPosition(o),
                                getRotation(o),
                                o.getFloat("fieldOfView", 60));
                        scene.setCam(cam);
                    }
                    break;
                case "Light":
                    String lightType = o.getString("lightType", "");
                    Light light = null;
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
                        default:
                            printWarning("Unsupported light of type '" + lightType + "' ");
                            break;
                    }

                    if (light != null) {
                        scene.addLight(light);
                        worldObjects.add(light);
                    }
                    break;
                case "Shape":
                    String shapeType = o.get("shapeType").asString();
                    switch (shapeType) {
                        case "Sphere":
                            Sphere sphere = new Sphere(getPosition(o), getRotation(o), getScale(o), o.getFloat("radius", 1));
                            wo = sphere;
                            scene.addPrimitive(sphere);
                            int materialId = o.getInt("materialID", -1);
                            if (materialId != -1) {
                                if (!requiredMaterial.containsKey(materialId)) {
                                    requiredMaterial.put(materialId, new ArrayList<>());
                                }
                                requiredMaterial.get(materialId).add(sphere);
                            }
                        break;
					default:
                            printWarning("Unsupported shape of type '" + shapeType + "'");
                    }
                    break;
                case "None":
                    EmptyObject eo = new EmptyObject(getPosition(o), getRotation(o), getScale(o));
                    wo = eo;
                    break;
                default:
                    printWarning("Unsupported object of type '" + type + "'");
            }

            if (parent != null && wo != null) {
                parent.addChild(wo);
            }

            if (wo != null) {
                worldObjects.add(wo);
                JsonArray childrenArray = o.get("children").asArray();
                if (childrenArray.size() != 0) {
                    addWorldObjects(childrenArray, worldObjects, requiredMaterial, wo, scene, cameraId);
                }
            }
        }
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
}
