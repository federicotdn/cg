package cg.accelerator;

import cg.render.BoundingBox;
import cg.render.Collision;
import cg.render.QuickCollision;
import cg.render.Ray;
import cg.render.assets.Mesh;
import cg.render.shapes.MeshInstance;

import java.util.*;

/**
 * Created by Hobbit on 5/4/16.
 */
public class MeshKDTree {
    private KDTreeNode root;
    private Mesh meshData;
    private BoundingBox worldBounds;

    public MeshKDTree(Mesh meshData, int triangleCount) {
        this.meshData = meshData;

        List<Integer> indexes = new ArrayList<>(triangleCount);
        for (int i = 0; i < triangleCount; i++) {
            indexes.add(i);
        }

        this.root = generateTree(indexes, 50, 0, 20);
    }

    private KDTreeNode generateTree(List<Integer> indexes, int threshold, int depth, int maxDepth) {
        int axis = depth % 3;


        if (indexes.size() <= threshold || depth >= maxDepth) {
            return new Leaf(indexes);
        }

        float location = getMedian(indexes, meshData, axis);

        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        float[] minMax = new float[2];
        for (int index : indexes) {
            meshData.calculateMinAndMax(index, axis, minMax);
            if (minMax[0] <= location) {
                left.add(index);
            }

            if (minMax[1] >= location) {
                right.add(index);
            }
        }

        INode node = new INode(location, axis);
        node.leftChild = generateTree(left, threshold, depth + 1, maxDepth);
        node.rightChild = generateTree(right, threshold, depth + 1, maxDepth);

        return node;
    }

    public QuickCollision hit(Ray ray, MeshInstance mesh) {
        Deque<StackNode> stack = new LinkedList<>();
        float tMax = ray.getMaxT();
        float tMin = 0;
        stack.push(new StackNode(root, 0, tMax));
        QuickCollision col = null;
        KDTreeNode first, second;
        while (!stack.isEmpty() && col == null) {
            StackNode sNode = stack.pop();
            KDTreeNode node = sNode.node;
            tMax = sNode.tMax;
            tMin = sNode.tMin;
            while (!node.isLeaf()) {
                INode iNode = (INode)node;
                float t = (iNode.location - ray.getOrigin().getCoordByAxis(iNode.axis))/ray.getDirection().getCoordByAxis(iNode.axis);
                if (ray.getOrigin().getCoordByAxis(iNode.axis) < iNode.location ||
                        (Math.abs(ray.getOrigin().getCoordByAxis(iNode.axis) - iNode.location) < 0.000001f
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
            col = checkCollision(ray, leaf.indexes, mesh);
            if (col != null && col.getLocalT() > tMax) {
                col = null;
            }
        }

        return col;
    }

    private class StackNode {
        public KDTreeNode node;
        public float tMin;
        public float tMax;

        public StackNode(KDTreeNode node, float tMin, float tMax) {
            this.node = node;
            this.tMax = tMax;
            this.tMin = tMin;
        }
    }

    private QuickCollision checkCollision(Ray ray, int[] indexes, MeshInstance mesh) {
        QuickCollision closestCol = null;
        for (int index : indexes) {
            QuickCollision col = meshData.checkCollision(ray, index, mesh);

            if (col == null) {
                continue;
            }

            if (closestCol == null || col.getLocalT() < closestCol.getLocalT()) {
                closestCol = col;
            }
        }

        return closestCol;
    }

    private float getMedian(List<Integer> triangleIndexes, Mesh mesh, int axis) {
        float[] avg = new float[triangleIndexes.size()];

        for (int i = 0; i < avg.length; i++) {
            avg[i] = mesh.getAvg(triangleIndexes.get(i), axis);
        }

        Arrays.sort(avg);

        if (avg.length % 2 == 1) {
            return avg[avg.length/2];
        } else {
            return (avg[(avg.length)/2 - 1] + avg[avg.length/2])/2.0f;
        }
    }

    private class Leaf implements KDTreeNode {
        public int[] indexes;

        @Override
        public boolean isLeaf() {
            return true;
        }

        public Leaf(List<Integer> indexes) {
            this.indexes = new int[indexes.size()];
            for (int i = 0; i < indexes.size(); i++) {
                this.indexes[i] = indexes.get(i);
            }
        }
    }
}
