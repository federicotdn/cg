package cg.render;

import cg.math.Vec3;

/**
 * Created by Hobbit on 4/30/16.
 */
public class EmptyObject extends WorldObject {
    public EmptyObject(Vec3 t, Vec3 r, Vec3 s) {
        setTransform(t, r, s);
    }
}
