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
    protected List<WorldObject> children;
    protected WorldObject parent;

    protected void setTransform(Vec3 t, Vec3 r, Vec3 s) {
        if (t == null) {
            t = new Vec3();
        }

        if (r == null) {
            r = new Vec3();
        }

        if (s == null) {
            s = new Vec3(1, 1, 1);
        }

        Matrix4 translation = Matrix4.transFromVec(t);
        Matrix4 rotX = Matrix4.rotationX(r.x);
        Matrix4 rotY = Matrix4.rotationY(r.y);
        Matrix4 rotZ = Matrix4.rotationZ(r.z);
        Matrix4 rot = rotZ.mul(rotY).mul(rotX);
        Matrix4 scale = Matrix4.scaleFromVec(s);

        this.transform = translation.mul(rot).mul(scale);
        this.invTransform = transform.inverse();
    }

    public Matrix4 getTransform() {
        if (parent == null) {
            return transform;
        }

        return parent.getTransform().mul(transform);
    }

    public void setParent(WorldObject parent) {
        this.parent = parent;
    }

    public void calculateTransform() {
        transform = getTransform();
        invTransform = transform.inverse();
    }

    public void addChild(WorldObject child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children.add(child);
        child.parent = this;
    }
}
