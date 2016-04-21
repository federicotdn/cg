package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hobbit on 4/21/16.
 */
public abstract class WorldObject {
    protected Matrix4 transform;
    protected Matrix4 invTransform;
    protected List<WorldObject> children = new ArrayList<>();
    protected WorldObject parent;

    public abstract void setTransform(Vec3 t, Vec3 r, Vec3 s);
}
