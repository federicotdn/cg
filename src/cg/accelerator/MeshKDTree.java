package cg.accelerator;

import cg.math.Vec3;
import cg.render.Collision;
import cg.render.Ray;
import cg.render.assets.Mesh;
import cg.render.shapes.InfinitePlane;
import cg.render.shapes.MeshInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hobbit on 5/4/16.
 */
public class MeshKDTree {
    private KDTreeNode root;
    private Mesh meshData;

    public MeshKDTree(Mesh meshData, int triangleCount) {
        this.meshData = meshData;

        List<Integer> indexes = new ArrayList<>(triangleCount);
        for (int i = 0; i < triangleCount; i++) {
            indexes.add(i);
        }

        this.root = generateTree(indexes, 30, 0);
    }

    private KDTreeNode generateTree(List<Integer> indexes, int threshold, int depth) {
        int axis = depth % 3;


        if (indexes.size() <= threshold) {
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
        node.leftChild = generateTree(left, threshold, depth + 1);
        node.rightChild = generateTree(right, threshold, depth + 1);

        return node;
    }

    public Collision hit(Ray ray, MeshInstance mesh) {
        return hit(ray, root, mesh);
    }

    private Collision hit(Ray ray, KDTreeNode node, MeshInstance mesh) {
        if (node.isLeaf()) {
            Leaf leaf = (Leaf)node;
            return checkCollision(ray, leaf.indexes, mesh);
        }

        INode iNode = (INode)node;
        KDTreeNode sameSideNode = (ray.getOrigin().getCoordByAxis(iNode.axis) < iNode.location ? iNode.leftChild : iNode.rightChild);

        Collision col = hit(ray,sameSideNode, mesh);

        if (col != null) {
            return col;
        }

        Float t = InfinitePlane.planeT(ray, Vec3.axisVec(iNode.axis), iNode.location);
        if (t != null) {
            return hit(ray, iNode.leftChild == sameSideNode ? iNode.rightChild : iNode.leftChild, mesh);
        }

        return null;
    }

    private Collision checkCollision(Ray ray, int[] indexes, MeshInstance mesh) {
        Collision closestCol = null;
        for (int index : indexes) {
            Collision col = meshData.checkCollision(ray, index, mesh);

            if (col == null || col.getT() > ray.getMaxT()) {
                continue;
            }

            if (closestCol == null || col.getT() < closestCol.getT()) {
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
            return (avg[(avg.length - 1)/2] + avg[avg.length/2])/2;
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
