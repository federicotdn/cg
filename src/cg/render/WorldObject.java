package cg.render;

import cg.math.Matrix4;
import cg.math.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hobbit on 4/21/16.
 */
public abstract class WorldObject {
    public Matrix4 transform;
    public Matrix4 invTransform;
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

        Matrix4 rot = getRotationMatrix(r);
        Matrix4 scale = Matrix4.scaleFromVec(s);

        this.transform = translation.mul(rot).mul(scale);
        this.invTransform = transform.inverse();
    }

    public void setParent(WorldObject parent) {
        this.parent = parent;
    }

    public void calculateTransform() {
        if (children != null) {
            for (WorldObject wo: children) {
                wo.calculateTransform(transform);
            }
        }
    }

    private void calculateTransform(Matrix4 parentTransform) {
        transform = parentTransform.mul(transform);
        invTransform = transform.inverse();
        calculateTransform();
    }

    public void addChild(WorldObject child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children.add(child);
        child.parent = this;
    }

    protected Matrix4 getRotationMatrix(Vec3 r) {
        Matrix4 rotX = Matrix4.rotationX(r.x);
        Matrix4 rotY = Matrix4.rotationY(r.y);
        Matrix4 rotZ = Matrix4.rotationZ(r.z);

       return rotY.mul(rotX).mul(rotZ);
    }
}
