package cg.accelerator;

import cg.math.Vec3;
import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.Primitive;
import cg.render.Ray;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Hobbit on 4/16/16.
 */
public class KDTree {
    private KDTreeNode root;
    private BoundingBox worldBounds;

    public KDTree(List<Primitive> primitives, int threshold) {
        Vec3 pMin = primitives.get(0).getBBox().pMin, pMax =  primitives.get(0).getBBox().pMax;

        for (Primitive primitive: primitives) {
            BoundingBox bbox = primitive.getBBox();
            pMin = pMin.min(bbox.pMin);
            pMax = pMax.max(bbox.pMax);
        }

        worldBounds = new BoundingBox(pMin, pMax);
        this.root = generateTree(primitives, threshold, 0, 16);
    }

    public Collision hit(Ray ray) {
        if (worldBounds.collide(ray) < 0) {
            return null;
        }

        Deque<StackNode> stack = new LinkedList<>();
        double tMax = ray.getMaxT();
        double tMin = 0;
        stack.push(new StackNode(root, 0, tMax));
        Collision col = null;
        KDTreeNode first, second;
        while (!stack.isEmpty() && col == null) {
            StackNode sNode = stack.pop();
            KDTreeNode node = sNode.node;
            tMax = sNode.tMax;
            tMin = sNode.tMin;
            while (!node.isLeaf()) {
                INode iNode = (INode)node;
                double t = (iNode.location - ray.getOrigin().getCoordByAxis(iNode.axis))/ray.getDirection().getCoordByAxis(iNode.axis);
                if (ray.getOrigin().getCoordByAxis(iNode.axis) <= iNode.location ||
                        (Math.abs(ray.getOrigin().getCoordByAxis(iNode.axis) - iNode.location) < 0.0001f
                                && ray.getDirection().getCoordByAxis(iNode.axis) < 0)) {
                    first = iNode.leftChild;
                    second = iNode.rightChild;
                } else {
                    first = iNode.rightChild;
                    second = iNode.leftChild;
                }
                if (t >= tMax || t <= 0)
                    node = first;
                else if (t < tMin)
                    node = second;
                else {
                    stack.push(new StackNode(second, t, tMax));
                    node = first;
                    tMax = t;
                }
            }

            Leaf leaf = (Leaf)node;
            col = checkCollision(ray, leaf.primitives);
            if (col != null && col.getT() > tMax) {
                col = null;
            }
        }

        return col;
    }

    private class StackNode {
        public KDTreeNode node;
        public double tMin;
        public double tMax;

        public StackNode(KDTreeNode node, double tMin, double tMax) {
            this.node = node;
            this.tMax = tMax;
            this.tMin = tMin;
        }
    }

    private Collision checkCollision(Ray ray, List<Primitive> primitives) {
        Collision closestCol = null;
        for (Primitive primitive : primitives) {
            double t = primitive.getBBox().collide(ray);
            if (t >= 0) {
                Collision col = primitive.collideWith(ray);

                if (col == null) {
                    continue;
                }

                if (closestCol == null || col.getT() < closestCol.getT()) {
                    closestCol = col;
                }
            }
        }

       return closestCol;
    }

    private KDTreeNode generateTree(List<Primitive> primitives, int threshold, int depth, int maxDepth) {
        int axis = depth % 3;
        if (primitives.size() <= threshold || depth == maxDepth) {
             return new Leaf(primitives);
        }

        sortByAxis(primitives, axis);

        double location = getMedian(primitives, axis);

        Predicate<Primitive> isLeft = p -> p.getBBox().pMin.getCoordByAxis(axis) <= location;
        Predicate<Primitive> isRight = p -> p.getBBox().pMax.getCoordByAxis(axis) > location;
        List<Primitive> leftPrimitives = primitives.stream().filter(isLeft).collect(Collectors.toList());
        List<Primitive> rightPrimitives = primitives.stream().filter(isRight).collect(Collectors.toList());

        INode node = new INode(location, axis);
        node.leftChild = generateTree(leftPrimitives, threshold, depth + 1, maxDepth);
        node.rightChild = generateTree(rightPrimitives, threshold, depth + 1, maxDepth);

        return node;
    }

    private void sortByAxis(List<Primitive> primitives, final int axis) {
        Collections.sort(primitives, (p1, p2) -> {
                    Vec3 c1 = p1.getBBox().getCenter();
                    Vec3 c2 = p2.getBBox().getCenter();
                    return new Double(c1.getCoordByAxis(axis)).compareTo(c2.getCoordByAxis(axis));
                }
        );
    }

    private double getMedian(List<Primitive> primitives, int axis) {
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
}
