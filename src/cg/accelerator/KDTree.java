package cg.accelerator;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;
import cg.render.shapes.InfinitePlane;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Hobbit on 4/16/16.
 */
public class KDTree {
    private KDTreeNode root;

    public KDTree(List<Primitive> primitives, int threshold) {
        this.root = generateTree(primitives, threshold, 0);
    }

    public Collision hit(Ray ray) {
        return hit(ray, root);
    }

    private Collision hit(Ray ray, KDTreeNode node) {
        if (node.isLeaf()) {
            Leaf leaf = (Leaf)node;
            return checkCollision(ray, leaf.primitives);
        }

        INode iNode = (INode)node;
        KDTreeNode sameSideNode = (ray.getOrigin().getCoordByAxis(iNode.axis) < iNode.location ? iNode.leftChild : iNode.rightChild);

        Collision col = hit(ray,sameSideNode);

        if (col != null) {
        	return col;
        }

        Float t = InfinitePlane.planeT(ray, Vec3.axisVec(iNode.axis).mul(ray.getOrigin().getCoordByAxis(iNode.axis) - iNode.location < 0 ? -1 : 1), iNode.location);
        if (t != null) {
        	return hit(ray, iNode.leftChild == sameSideNode ? iNode.rightChild : iNode.leftChild);
        }
        
        return null;
    }

    private Collision checkCollision(Ray ray, List<Primitive> primitives) {
        Collision closestCol = null;
        for (Primitive primitive : primitives) {
            Float t = primitive.getBBox().collide(ray);
            if (t != null) {
                Collision col = primitive.collideWith(ray);

                if (col == null || col.getT() > ray.getMaxT()) {
                    continue;
                }

                if (closestCol == null || col.getT() < closestCol.getT()) {
                    closestCol = col;
                }
            }
        }

       return closestCol;
    }

    private KDTreeNode generateTree(List<Primitive> primitives, int threshold, int depth) {
        int axis = depth % 3;
        sortByAxis(primitives, axis);


        if (primitives.size() <= threshold) {
            return new Leaf(primitives);
        }

        float location = getMedian(primitives, axis);

        Predicate<Primitive> isLeft = p -> p.getBBox().getCenter().getCoordByAxis(axis) < location ||
        		(p.getBBox().pMin.getCoordByAxis(axis) >= location && p.getBBox().pMax.getCoordByAxis(axis) < location);
        Predicate<Primitive> isRight = p -> p.getBBox().getCenter().getCoordByAxis(axis) >= location ||
        		(p.getBBox().pMin.getCoordByAxis(axis) >= location && p.getBBox().pMax.getCoordByAxis(axis) < location);
        List<Primitive> leftPrimitives = primitives.stream().filter(isLeft).collect(Collectors.toList());
        List<Primitive> rightPrimitives = primitives.stream().filter(isRight).collect(Collectors.toList());

        INode node = new INode(location, axis);
        node.leftChild = generateTree(leftPrimitives, threshold, depth + 1);
        node.rightChild = generateTree(rightPrimitives, threshold, depth + 1);

        return node;
    }

    private void sortByAxis(List<Primitive> primitives, final int axis) {
        Collections.sort(primitives, (p1, p2) -> {
                    Vec3 c1 = p1.getBBox().getCenter();
                    Vec3 c2 = p2.getBBox().getCenter();
                    return new Float(c1.getCoordByAxis(axis)).compareTo(c2.getCoordByAxis(axis));
                }
        );
    }

    private float getMedian(List<Primitive> primitives, int axis) {
        Vec3 medianLoc;

        if (primitives.size() % 2 == 1) {
            medianLoc = primitives.get(primitives.size()/2).getBBox().getCenter();
        } else {
            Vec3 c1 = primitives.get(primitives.size() / 2 - 1).getBBox().getCenter();
            Vec3 c2 = primitives.get(primitives.size() / 2).getBBox().getCenter();
            medianLoc = c1.sum(c2).mul(0.5f);
        }

        return medianLoc.getCoordByAxis(axis);
    }


    private class INode implements KDTreeNode {
        public int axis;
        public float location;
        public KDTreeNode leftChild;
        public KDTreeNode rightChild;

        public INode(float location, int axis) {
            this.location = location;
            this.axis = axis;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    private class Leaf implements KDTreeNode {
        public List<Primitive> primitives;

        @Override
        public boolean isLeaf() {
            return true;
        }

        public Leaf(List<Primitive> primitives) {
            this.primitives = primitives;
        }
    }

    private interface KDTreeNode {
        boolean isLeaf();
    }
}
